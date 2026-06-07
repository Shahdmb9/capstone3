package org.example.capstone3.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.DTO.OUT.AiHabitDTOout;
import org.example.capstone3.DTO.OUT.HabitSummaryDTOout;
import org.example.capstone3.DTO.OUT.TodayHabitDTO;
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

    //creating logs for the users evreyday
    @Scheduled(cron = "0 0 0 * * *")
    public void evaluateHabitsDaily() {
        List<Habit> allHabits = habitRepository.findByIsAiSuggestedFalse();
        for (Habit habit : allHabits) {
            HabitLog log = createHabitLog(habit);
            habitLogRepository.save(log);
        }
    }

    //alerting to complete the habit
    @Scheduled(cron = "0 0 18 * * *")
    public void alert() {
        List<HabitLog> allHabitsLog = habitLogRepository.findHabitLogByApprovalStatusAndCreatedAt("NOT_STARTED",LocalDate.now());
        for (HabitLog habitlog : allHabitsLog) {
            Habit habit=habitRepository.findHabitById(habitlog.getHabit().getId());
            if(habit.getIndividual()!=null) {
                Individual individual = habit.getIndividual();
                sendAlertMessage(individual.getPhoneNumber());
            }
            if (habit.getParent() != null) {
                Parent parent = habit.getParent();
                sendAlertMessageParent(parent.getPhoneNumber());
            }
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

    public void addHabitIndividual(Integer individualId, Integer categoryId, IndividualHabitDTO dto) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) throw new ApiException("Individual not found");

        Category category = categoryRepository.findCategoryById(categoryId); // جلب من الباث مباشرة
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
        if (dto.getPoints() != null) habit.setPoints(dto.getPoints());

        HabitLog habitLog = new HabitLog(null, null, "NOT_STARTED", null, LocalDate.now(), habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);
    }

    public void addHabitParent(Integer parentId, Integer childId, Integer categoryId, Habit habitIn) {
        Parent parent = getParent(parentId);
        Child child = getChild(childId);
        if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) throw new ApiException("Category not found");

        Habit habit = modelMapper.map(habitIn, Habit.class);
        if (habit.getPoints() == null || habit.getPoints() == 0) habit.setPoints(10);
        habit.setStreak(0);
        habit.setHighestStreak(0);
        habit.setFrequency("DAILY");
        habit.setIsAiSuggested(false);
        habit.setParent(parent);
        habit.setChild(child);
        habit.setCategory(category);

        HabitLog habitLog = new HabitLog(null, null, "NOT_STARTED", null, LocalDate.now(), habit);

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

    public List<TodayHabitDTO> getTodayHabitsForChild(Integer childId) {
        Child child = getChild(childId);
        List<TodayHabitDTO> result = new ArrayList<>();
        for (Habit habit : child.getHabit()) {
            HabitLog log = habitLogRepository.findByHabitAndCreatedAt(habit, LocalDate.now());
            result.add(new TodayHabitDTO(habit.getId(),log.getId(), habit.getTitle(),
                    habit.getDescription(), habit.getPoints(), habit.getStreak(),habit.getHighestStreak(), log.getApprovalStatus()));
        }
        return result;
    }

    public List<TodayHabitDTO> getTodayHabitsForIndividual(Integer individualId) {
        Individual individual = getIndividual(individualId);
        List<TodayHabitDTO> result = new ArrayList<>();
        for (Habit habit : individual.getHabits()) {
            if(habit.getIsAiSuggested()==false) {
                HabitLog log = habitLogRepository.findByHabitAndCreatedAt(habit, LocalDate.now());
                result.add(new TodayHabitDTO(habit.getId(), log.getId(), habit.getTitle(),
                        habit.getDescription(), habit.getPoints(), habit.getStreak(), habit.getHighestStreak(), log.getApprovalStatus()));
            }
        }
        return result;

    }

    public List<TodayHabitDTO> getHabitByDateIndividual(Integer individualId, LocalDate date) {
        Individual individual = getIndividual(individualId);
        List<TodayHabitDTO> result = new ArrayList<>();
        for (Habit habit : individual.getHabits()) {
            if(habit.getIsAiSuggested()==false) {
                HabitLog log = habitLogRepository.findByHabitAndCreatedAt(habit, date);
                result.add(new TodayHabitDTO(habit.getId(), log.getId(), habit.getTitle(),
                        habit.getDescription(), habit.getPoints(), habit.getStreak(), habit.getHighestStreak(), log.getApprovalStatus()));
            }
            }
        return result;
    }

    public List<TodayHabitDTO> getHabitByDateChild(Integer childId, LocalDate date) {
        Child child = getChild(childId);
        List<TodayHabitDTO> result = new ArrayList<>();
        for (Habit habit : child.getHabit()) {
            HabitLog log = habitLogRepository.findByHabitAndCreatedAt(habit, date);
            result.add(new TodayHabitDTO(habit.getId(),log.getId(), habit.getTitle(),
                    habit.getDescription(), habit.getPoints(), habit.getStreak(),habit.getHighestStreak(), log.getApprovalStatus()));
        }
        return result;
    }

    public void deleteAllAiSuggested(Integer individualId){
        Individual individual=getIndividual(individualId);
        List<Habit> habit=habitRepository.findHabitByIsAiSuggestedTrueAndIndividualId(individualId);
        if(habit.isEmpty())
            throw new ApiException("No AI Habit found");
        habitRepository.deleteAllAiSuggestedTrue();
    }

    public void logHabit(Integer habitLogId) {
        HabitLog log = getHabitLogById(habitLogId);
        Habit habit = getHabitById(log.getHabit().getId());

        if (isAlreadyCompleeted(habit) != null) {
            throw new ApiException("Habit already logged for this " + habit.getFrequency() + " period");
        }

        log.setLoggedDate(LocalDate.now());
        boolean isChild = habit.getChild() != null;
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
            Child child = habit.getChild();
            String name=child.getFullName();
            String topic="your child: "+ name +"just update the habit status he need your conformation";
            String tone="notifyong the parent about pending request";
            String lang="arabic";
            String message=aiService.generateWhatsAppMessage(topic,tone,lang);
            Parent parent=getParent(habit.getParent().getId());
            whatsAppService.whatsAppMessage("0542381757",message);
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
        if (!status.matches("^(NOT_STARTED|PENDING|COMPLETED|REJECTED)$"))
            throw new ApiException("Invalid status");

        Habit habit = getHabitById(habitId);
        if (habit.getParent() == null) throw new ApiException("This is not a child habit");

        HabitLog log = habitLogRepository.findHabitLogByHabit(habit);
        Parent parent = getParent(parentId);

        if (!parentId.equals(log.getHabit().getParent().getId()))
            throw new ApiException("You are not the owner of this habit");

        if (!parent.getChildren().contains(log.getHabit().getChild()))
            throw new ApiException("This is not your child");

        if (!log.getApprovalStatus().equals("PENDING")) {
            throw new RuntimeException("You already checked this habit");
        }

        log.setApprovalStatus(status);
        log.setApprovedAt(LocalDate.now());
        habitLogRepository.save(log);

        if ("COMPLETED".equals(status)) {
            Child child = log.getHabit().getChild();
            if (child != null) {
                child.setPoints(child.getPoints() + log.getHabit().getPoints());
                childRepository.save(child);
                updateStreak(log.getHabit());

                if (log.getHabit().getReward() != null) {
                    Reward reward = log.getHabit().getReward();
                    if (child.getPoints() >= reward.getRequiredPoints()) {
                        child.setPoints(child.getPoints() - reward.getRequiredPoints());

                        reward.setClaimedAt(java.time.LocalDateTime.now());

                        rewardRepository.save(reward);                    }
                }
            }
            childRepository.save(child);
            habitRepository.save(log.getHabit());
        }
    }

    public void updateStreak(Habit habit) {
        if (habit.getStreak() == null) habit.setStreak(0);
        if (habit.getHighestStreak() == null) habit.setHighestStreak(0);

        LocalDate today = LocalDate.now();

        List<HabitLog> recentLogs = habitLogRepository.findByHabitAndApprovalStatusOrderByLoggedDateDesc(habit, "COMPLETED");

        // Find the most recent log that is except today
        LocalDate lastLogDate = null;
        for (HabitLog log : recentLogs) {
            if (log.getLoggedDate().isBefore(today)) {
                lastLogDate = log.getLoggedDate();
                break;
            }
        }

        boolean streakContinues = false;
        if (lastLogDate != null) {
            streakContinues = lastLogDate.isEqual(today.minusDays(1));
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

    public HabitLog getHabitLogById(Integer logId) {
        HabitLog log = habitLogRepository.findHabitLogById(logId);
        if (log == null) throw new ApiException("Log not found");
        return log;
    }


    public Habit getHabitById(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null)
            throw new ApiException("Habit not found");

        return habit;
    }

    public HabitSummaryDTOout getHabitSummary(Integer habitId) {
        Habit habit = getHabitById(habitId);
        Integer completedDays=habitLogRepository.findByHabitAndApprovalStatus(habit,"COMPLETED").size();
        Integer missedDays=habitLogRepository.findByHabitAndApprovalStatus(habit,"NOT_STARTED").size();

        return new HabitSummaryDTOout(habit.getId(), habit.getTitle(),
                habit.getDescription(), habit.getPoints(), habit.getStreak(),habit.getHighestStreak(),missedDays,completedDays,habit.getCategory().getName());
    }

    public void acceptHabitSuggestedByAI(Integer individualId, Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");
        Individual individual = getIndividual(individualId);
        if (individual != habit.getIndividual()) throw new ApiException("This is not your habit");
        habit.setIsAiSuggested(false);
        HabitLog habitLog = new HabitLog(null, null, "NOT_STARTED", null, LocalDate.now(), habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);

    }

    public HabitLog createHabitLog(Habit habit) {
        HabitLog habitLog = new HabitLog(null, null, "NOT_STARTED", null, LocalDate.now(), habit);
        habitLogRepository.save(habitLog);
        return habitLog;
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

    public List<AiHabitDTOout> generateHabits(Integer individualId) {

        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }
        //grab user intrest
        List<Category> interest=new ArrayList<>(individual.getCategories());
        if(interest.isEmpty())
            throw new ApiException("No interest found");

        String prompt = "Generate 5 habits for the following categories:\n" +
                "- " + interest.toString() + "\n" +
                "Respond ONLY with a raw JSON object containing:\n" +
                "1. 'title': e.g., 'Exercise'\n" +
                "2. 'description': e.g., 'Regular exercise for 30 minutes'\n" +
                "3. 'frequency': e.g., 'DAILY' only DAILY\n" +
                "4. 'points': e.g., 10\n"
                +"Respond ONLY with raw JSON.";

        String result=aiService.callClaudeApi(prompt);
        ObjectMapper mapper = new ObjectMapper();
        List<Habit> routines=new ArrayList<>();
        try {
            // convert json array string to List<Routine>
             routines = mapper.readValue(result, new TypeReference<List<Habit>>(){});

            // Print the parsed titles
            for (Habit routine : routines) {
                Habit habit=modelMapper.map(routine,Habit.class);
                habit.setIndividual(individual);
                String cateName=aiService.callClaudeApi("classify this habit by name "+habit.getDescription()+" to one of :"+interest.toString()+"as : 'name': e.g., 'sport'");
//                habit.setCategory(categoryRepository.findCategoryByNameIgnoreCase(new JSONObject(cateName).getString("name")));
                habit.setIsAiSuggested(true);
                habit.setStreak(0);
                habit.setHighestStreak(0);
                habitRepository.save(habit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<AiHabitDTOout> resultList=new ArrayList<>();
        for (Habit routine : routines) {
            resultList.add(modelMapper.map(routine,AiHabitDTOout.class));
        }
        return resultList ;
    }

    public HabitLog isAlreadyCompleeted(Habit habit) {
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

    public String getHabitCommitmentAnalysis(Integer individualId, Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");

        if (habit.getIndividual() == null) {
            throw new ApiException("Commitment analysis is exclusively available for Individual habits only");
        }

        if (!habit.getIndividual().getId().equals(individualId)) {
            throw new ApiException("Access Denied: This habit does not belong to you");
        }

        List<HabitLog> logs = habitLogRepository.findByHabitAndApprovalStatusOrderByLoggedDateDesc(habit, "COMPLETED");
        int completedCount = logs.size();

        String prompt = "Analyze commitment for Habit:\n- Title: " + habit.getTitle() + "\n- Total Successful Logs: " + completedCount + "\nReturn JSON with commitmentPercentage, actionRequired.";
        return aiService.callClaudeApi(prompt);
    }

    public String getHabitImprovementAdvisor(Integer individualId, Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");

        if (habit.getIndividual() == null) {
            throw new ApiException("Improvement advice is exclusively available for Individual habits only");
        }

        if (!habit.getIndividual().getId().equals(individualId)) {
            throw new ApiException("Access Denied: This habit does not belong to you");
        }

        String categoryName = (habit.getCategory() != null) ? habit.getCategory().getName() : "General";

        List<HabitLog> completedLogs = habitLogRepository.findByHabitAndApprovalStatus(habit, "COMPLETED");
        int totalCompletedCount = completedLogs != null ? completedLogs.size() : 0;
        int currentStreak = habit.getStreak() != null ? habit.getStreak() : 0;

        String prompt = "Analyze this habit data: Name: " + habit.getTitle() + ", Category: " + categoryName +
                ", Current Streak: " + currentStreak + ", Total Completed: " + totalCompletedCount + ".\n" +
                "Return ONLY a raw JSON object with exactly these fields. Do not include markdown or text before/after:\n" +
                "{\n" +
                "  \"habitName\": \"" + habit.getTitle() + "\",\n" +
                "  \"categoryName\": \"" + categoryName + "\",\n" +
                "  \"threeImprovementTips\": [\"First specific tip based on logs\", \"Second specific tip\", \"Third specific tip\"]\n" +
                "}";

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
    public void sendAlertMessage(String number){

        String topic="you have uncompleted habit dont forget to complete it";
        String tone="alerting and reminder to complete the habit";
        String lang="arabic";
        String message=aiService.generateWhatsAppMessage(topic,tone,lang);
        whatsAppService.whatsAppMessage(number,message);
    }

    public void sendAlertMessageParent(String number){

        String topic="your child have uncompleted habit remind him to complete it";
        String tone="alerting and reminder to complete the habit";
        String lang="arabic";
        String message=aiService.generateWhatsAppMessage(topic,tone,lang);
        whatsAppService.whatsAppMessage(number,message);
    }
}
