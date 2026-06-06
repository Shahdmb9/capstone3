package org.example.capstone3.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.*;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final IndividualRepository individualRepository;
    private final CategoryRepository categoryRepository;
    private final HabitLogRepository habitLogRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    private final AiService aiService;
    private final RewardRepository rewardRepository;
    private final BadgeRepository badgeRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void evaluateHabitsDaily() {
        List<Habit> allHabits = habitRepository.findAll();
        for (Habit habit : allHabits) {
            HabitLog log = new HabitLog(null, LocalDate.now(), "NOT_STARTED", null, null, habit);
            habitLogRepository.save(log);
        }
    }

    public List<Habit> getIndividualHabits(Integer individualId) {
        return habitRepository.findHabitByIsAiSuggestedFalseAndIndividualId(individualId);
    }

    public List<Habit> getParentHabits(Integer parentId) {
        getParent(parentId);
        return habitRepository.findByParentId(parentId);
    }

    public List<Habit> getChildHabits(Integer childId) {
        getChild(childId);
        return habitRepository.findByChildId(childId);
    }

    public void addHabitIndividual(Integer individualId, IndividualHabitDTO dto) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Category category = categoryRepository.findCategoryById(dto.getCategoryId());
        if (category == null) throw new ApiException("Category not found");

        Habit habit = new Habit();
        habit.setTitle(dto.getTitle());
        habit.setDescription(dto.getDescription());
        habit.setFrequency("DAILY");
        habit.setIsAiSuggested(false);
        habit.setIndividual(individual);
        habit.setCategory(category);
        habit.setStreak(0);
        habit.setHighestStreak(0);

        HabitLog habitLog = new HabitLog(null, LocalDate.now(), "NOT_STARTED", null, null, habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);
    }

    public void addHabitParent(Integer parentId, Integer childId, Habit habitIn) {
        Parent parent = getParent(parentId);
        Child child = getChild(childId);

        if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

        Habit habit = modelMapper.map(habitIn, Habit.class);
        if (habit.getPoints() == null || habit.getPoints() == 0) habit.setPoints(10);
        habit.setStreak(0);
        habit.setHighestStreak(0);
        habit.setIsAiSuggested(false);
        habit.setParent(parent);
        habit.setChild(child);

        HabitLog habitLog = new HabitLog(null, LocalDate.now(), "NOT_STARTED", null, null, habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);
    }

    public void updateHabit(Integer habitId, IndividualHabitDTO dto) {
        Habit oldHabit = habitRepository.findHabitById(habitId);
        if (oldHabit == null) throw new ApiException("Habit not found");

        Category category = categoryRepository.findCategoryById(dto.getCategoryId());
        if (category == null) throw new ApiException("Category not found");

        oldHabit.setTitle(dto.getTitle());
        oldHabit.setDescription(dto.getDescription());
        oldHabit.setFrequency(dto.getFrequency().toUpperCase());
        oldHabit.setCategory(category);

        habitRepository.save(oldHabit);
    }

    public void deleteHabit(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");
        habitRepository.delete(habit);
    }

    public void logHabit(Integer habitLogId) {
        HabitLog log = getHabitLogById(habitLogId);
        Habit habit = getHabitById(log.getHabit().getId());

        if (isAlreadyLoggedInPeriod(habit) != null) {
            throw new RuntimeException("Habit already logged for this " + habit.getFrequency() + " period");
        }

        log.setLoggedDate(LocalDate.now());
        boolean isChild = (habit.getChild() != null);

        log.setApprovalStatus(isChild ? "PENDING" : "COMPLETED");
        habitLogRepository.save(log);

        if (!isChild) {
            updateStreak(habit);
            Individual individual = habit.getIndividual();

            if (individual != null) {
                individual.setPoints(individual.getPoints() + habit.getPoints());
                individualRepository.save(individual);

                checkAndAssignBadgesToIndividual(individual);
            }
        } else {
            if (habit.getParent() != null) {
                Parent parent = getParent(habit.getParent().getId());
                whatsAppService.sendReportByWhatsApp(parent.getPhoneNumber(), parent.getFullName());
            }
        }

        habitLogRepository.save(log);
        habitRepository.save(habit);
    }

    private void checkAndAssignBadgesToIndividual(Individual individual) {
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            if (individual.getPoints() >= badge.getPointsRequired()) {

                if (!individual.getBadges().contains(badge)) {

                    individual.getBadges().add(badge);
                    badge.getIndividuals().add(individual);

                    badgeRepository.save(badge);
                    System.out.println("Badge '" + badge.getTitle() + "' successfully awarded to: " + individual.getFullName());
                }
            }
        }
        individualRepository.save(individual);
    }



    public void reviewChildLog(Integer parentId, Integer habitId, String status) {
        if (!status.matches("^(NOT_STARTED|PENDING|COMPLETED|REJECTED)$")) {
            throw new ApiException("Invalid status");
        }

        Habit habit = getHabitById(habitId);

        if (habit.getParent() == null || habit.getChild() == null) {
            throw new ApiException("This is not a child habit");
        }

        HabitLog log = habitLogRepository.findHabitLogByHabit(habit);
        if (log == null) {
            throw new ApiException("No log found for this habit");
        }

        if (!parentId.equals(habit.getParent().getId())) {
            throw new ApiException("You are not the owner of this habit");
        }

        Parent parent = getParent(parentId);
        if (!parent.getChildren().contains(habit.getChild())) {
            throw new ApiException("This is not your child");
        }

        if (!"PENDING".equals(log.getApprovalStatus())) {
            throw new ApiException("You already checked this habit log");
        }

        log.setApprovalStatus(status);
        log.setApprovedAt(LocalDate.now());
        habitLogRepository.save(log);

        if ("COMPLETED".equals(status)) {
            Child child = habit.getChild();

            child.setPoints(child.getPoints() + habit.getPoints());
            updateStreak(habit);

            if (habit.getReward() != null) {
                Reward reward = habit.getReward();

                if (child.getPoints() >= reward.getRequiredPoints()) {
                    child.setPoints(child.getPoints() - reward.getRequiredPoints());

                    reward.setClaimedAt(java.time.LocalDateTime.now());

                    rewardRepository.save(reward);
                    System.out.println("Reward '" + reward.getTitle() + "' successfully claimed by child: " + child.getFullName());
                }
            }

            childRepository.save(child);
            habitRepository.save(habit);
        }
    }


    public void updateStreak(Habit habit) {
        if (habit.getStreak() == null) habit.setStreak(0);
        if (habit.getHighestStreak() == null) habit.setHighestStreak(0);

        LocalDate today = LocalDate.now();
        List<HabitLog> recentLogs = habitLogRepository.findByHabitAndApprovalStatusOrderByLoggedDateDesc(habit, "COMPLETED");

        LocalDate lastLogDate = null;
        for (HabitLog log : recentLogs) {
            if (log.getLoggedDate().isBefore(today)) {
                lastLogDate = log.getLoggedDate();
                break;
            }
        }

        boolean streakContinues = false;
        if (lastLogDate != null) {
            streakContinues = isConsecutivePeriod(lastLogDate, today, habit.getFrequency());
        }

        if (streakContinues) {
            habit.setStreak(habit.getStreak() + 1);
        } else {
            habit.setStreak(1);
        }

        if (habit.getStreak() > habit.getHighestStreak()) {
            habit.setHighestStreak(habit.getStreak());
        }
    }

    private boolean isConsecutivePeriod(LocalDate lastLog, LocalDate today, String frequency) {
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return lastLog.isEqual(today.minusDays(1));
            case "WEEKLY":
                LocalDate thisWeekStart = today.with(DayOfWeek.SUNDAY);
                LocalDate lastWeekStart = thisWeekStart.minusWeeks(1);
                LocalDate lastWeekEnd = thisWeekStart.minusDays(1);
                return !lastLog.isBefore(lastWeekStart) && !lastLog.isAfter(lastWeekEnd);
            case "MONTHLY":
                LocalDate thisMonthStart = today.withDayOfMonth(1);
                LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
                LocalDate lastMonthEnd = thisMonthStart.minusDays(1);
                return !lastLog.isBefore(lastMonthStart) && !lastLog.isAfter(lastMonthEnd);
            case "YEARLY":
                LocalDate thisYearStart = today.withDayOfYear(1);
                LocalDate lastYearStart = thisYearStart.minusYears(1);
                LocalDate lastYearEnd = thisYearStart.minusDays(1);
                return !lastLog.isBefore(lastYearStart) && !lastLog.isAfter(lastYearEnd);
            default:
                return false;
        }
    }

    public HabitLog getHabitLogById(Integer logId) {
        HabitLog log = habitLogRepository.findHabitLogById(logId);
        if (log == null) throw new ApiException("Log not found");
        return log;
    }

    /*
    public Map<String, Integer> IndividualStreakPerHabit(Integer individualId) {
        Individual individual = getIndividual(individualId);
        Map<String, Integer> streakMap = new HashMap<>();
        for (Habit habit : individual.getHabits()) {
            List<HabitLog> approvedLogs = habitLogRepository.findByHabitAndApprovalStatus(habit, "COMPLETED");
            List<LocalDate> dates = new ArrayList<>();
            for (HabitLog log : approvedLogs) {
                LocalDate loggedDate = log.getLoggedDate();
                if (!dates.contains(loggedDate)) dates.add(loggedDate);
            }
            dates.sort(Comparator.reverseOrder());
            int streak = 0;
            if (!dates.isEmpty()) {
                streak = 1;
                int allowedGap = getAllowedGap(habit.getFrequency());
                for (int i = 0; i < dates.size() - 1; i++) {
                    long daysBetween = ChronoUnit.DAYS.between(dates.get(i + 1), dates.get(i));
                    if (daysBetween <= allowedGap) streak++;
                    else break;
                }
            }
            streakMap.put(habit.getTitle(), streak);
        }
        return streakMap;
    }

    private int getAllowedGap(String frequency) {
        switch (frequency.toUpperCase()) {
            case "WEEKLY": return 7;
            case "MONTHLY": return 30;
            case "YEARLY": return 365;
            default: return 1;
        }
    }
    */

    public Habit getHabitById(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new RuntimeException("Habit not found");
        return habit;
    }

    public void acceptHabitSuggestedByAI(Integer individualId, Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");
        Individual individual = getIndividual(individualId);
        if (individual != habit.getIndividual()) throw new ApiException("This is not your habit");
        habit.setIsAiSuggested(false);
        createHabitLog(habitId);
        habitRepository.save(habit);
    }

    public void createHabitLog(Integer habitId) {
        Habit habit = getHabitById(habitId);
        HabitLog habitLog = new HabitLog(null, null, "NOT_STARTED", null, LocalDate.now(), habit);
        habitLogRepository.save(habitLog);
    }

    public List<Habit> AISuggestedHabit(Integer individualId) {
        getIndividual(individualId);
        return habitRepository.findHabitByIsAiSuggestedTrueAndIndividualId(individualId);
    }

    public List<Habit> getHabitsByCategory(Integer categoryId) {
        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) throw new ApiException("Category not found");
        return habitRepository.findHabitsByCategory_Id(categoryId);
    }

    public String generateHabits(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");
        List<Category> interest = new ArrayList<>(individual.getCategories());
        if (interest.isEmpty()) throw new ApiException("No interest found");

        String prompt = "Generate 5 habits for the following categories:\n- " + interest.toString() + "\nRespond in raw JSON object list format.";
        String result = aiService.callClaudeApi(prompt);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Habit> routines = mapper.readValue(result, new TypeReference<List<Habit>>() {
            });
            for (Habit routine : routines) {
                Habit habit = modelMapper.map(routine, Habit.class);
                habit.setIndividual(individual);
                String cateName = aiService.callClaudeApi("classify this habit: " + habit.getDescription() + " to one of " + interest.toString());
                habit.setCategory(categoryRepository.findCategoryByNameIgnoreCase(new JSONObject(cateName).getString("name")));
                habit.setIsAiSuggested(true);
                habitRepository.save(habit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public HabitLog isAlreadyLoggedInPeriod(Habit habit) {
        LocalDate now = LocalDate.now();
        LocalDate start = getPeriodStart(now, habit.getFrequency());
        List<HabitLog> logs = habitLogRepository.findHabitLogByHabitIdAndLoggedDateBetweenAndApprovalStatus(habit.getId(), start, now, "COMPLETED");
        return (logs != null && !logs.isEmpty()) ? logs.get(0) : null;
    }

    public LocalDate getPeriodStart(LocalDate date, String frequency) {
        switch (frequency) {
            case "DAILY":
                return date;
            case "WEEKLY":
                return date.with(DayOfWeek.SUNDAY);
            case "MONTHLY":
                return date.withDayOfMonth(1);
            default:
                return date;
        }
    }

    public Individual getIndividual(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");
        return individual;
    }

    public Parent getParent(Integer parentId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        return parent;
    }

    public Child getChild(Integer childId) {
        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");
        return child;
    }

    public String getHabitCommitmentAnalysis(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");

        List<HabitLog> logs = habitLogRepository.findByHabitAndApprovalStatusOrderByLoggedDateDesc(habit, "COMPLETED");
        int completedCount = logs.size();

        String prompt = "Analyze commitment for Habit:\n- Title: " + habit.getTitle() + "\n- Total Successful Logs: " + completedCount + "\nReturn JSON with commitmentPercentage, streakStatus, actionRequired.";
        return aiService.callClaudeApi(prompt);
    }

    public String getHabitImprovementAdvisor(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");

        List<HabitLog> completedLogs = habitLogRepository.findByHabitAndApprovalStatus(habit, "COMPLETED");
        String prompt = "Provide optimization advice for Habit: " + habit.getTitle() + "\nReturn JSON with currentStatus, improvementTips, streakMilestoneGoal.";
        return aiService.callClaudeApi(prompt);
    }

    public String riskPrediction(Integer id) {
        Habit habit = habitRepository.findHabitById(id);
        if (habit == null) throw new RuntimeException("Habit not found");
        return aiService.callClaudeApi(aiService.buildPromptRiskPrediction(habit));
    }

    public String BestHabitTime(Integer id) {
        Habit habit = habitRepository.findHabitById(id);
        if (habit == null) throw new RuntimeException("Habit not found");
        return aiService.callClaudeApi(aiService.buildPromptBestTime(habit));
    }
}
