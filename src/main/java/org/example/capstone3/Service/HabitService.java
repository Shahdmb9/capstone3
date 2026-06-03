package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.*;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    public List<Habit> getIndividualHabits(Integer individualId) {
        return habitRepository.findByIndividualId(individualId);
    }

    public List<Habit> getParentHabits(Integer parentId) {
        Parent parent = getParent(parentId);

        return habitRepository.findByParentId(parentId);
    }

    public List<Habit> getChildHabits(Integer childId) {
        Child child = getChild(childId);
        return habitRepository.findByChildId(childId);
    }

    public void addHabitIndividual(Integer individualId,IndividualHabitDTO dto) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }

        Category category = categoryRepository.findCategoryById(dto.getCategoryId());
        if (category == null) {
            throw new ApiException("Category not found");
        }

        Habit habit = new Habit();
        habit.setTitle(dto.getTitle());
        habit.setDescription(dto.getDescription());
        habit.setFrequency(dto.getFrequency().toUpperCase());
        habit.setIsAiSuggested(false);
        habit.setIndividual(individual);
        habit.setCategory(category);

        HabitLog habitLog = new HabitLog(null,null,"NOT_STARTED",null,LocalDate.now(),habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);

    }

    public void addHabitParent(Integer parentId, Integer childId, Habit habit) {

        Parent parent = getParent(parentId);
        Child child = getChild(childId);

        if (!parent.getChildren().contains(child))
            throw new ApiException("This is not your child");

        habit.setParent(parent);
        habit.setChild(child);

        HabitLog habitLog = new HabitLog(null,null,"NOT_STARTED",null,LocalDate.now(),habit);

        habitRepository.save(habit);
        habitLogRepository.save(habitLog);

    }

    public void updateHabit(Integer habitId, IndividualHabitDTO dto) {
        Habit oldHabit = habitRepository.findHabitById(habitId);
        if (oldHabit == null) {
            throw new ApiException("Habit not found");
        }

        Category category = categoryRepository.findCategoryById(dto.getCategoryId());
        if (category == null) {
            throw new ApiException("Category not found");
        }

        oldHabit.setTitle(dto.getTitle());
        oldHabit.setDescription(dto.getDescription());
        oldHabit.setFrequency(dto.getFrequency().toUpperCase());
        oldHabit.setCategory(category);

        habitRepository.save(oldHabit);
    }

    public void deleteHabit(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) {
            throw new ApiException("Habit not found");
        }
        habitRepository.delete(habit);
    }

    public void logHabit(Integer habitId) {
        Habit habit = getHabitById(habitId);

        // check if the habit is already logged for the current period if he's done a weekly habit, he can't do it again till the next week
        if (isAlreadyLoggedInPeriod(habit)!=null) {
            throw new RuntimeException("Habit already logged for this " + habit.getFrequency() + " period");
        }

        HabitLog log = habitLogRepository.findHabitLogByHabit(habit);
        log.setLoggedDate(LocalDate.now());

        // Auto-approve if it's an individual habit (no parent needed)
        boolean isChild = habit.getChild() != null;
        //if yes set completed status to pending if not approved
        log.setApprovalStatus(isChild ? "PENDING" : "COMPLETED");

        // Give points without approval if not child
        if (!isChild) {
            Individual individual = habit.getIndividual();
            individual.setPoints(individual.getPoints() + habit.getPoints());
            individualRepository.save(individual);
        }
        else {
            //email to the parent telling him about the child's habit'
        }

         habitLogRepository.save(log);
    }


    public void reviewChildLog(Integer ParentId,Integer habitId, String status) {
        if(!status.matches("^(NOT_STARTED|PENDING|COMPLETED|REJECTED)$"))
            throw new ApiException("Invalid status");

        Habit habit =getHabitById(habitId);

        if(habit.getParent()==null)
            throw new ApiException("This is not a child habit");

        HabitLog log =habitLogRepository.findHabitLogByHabit(habit);

        Parent parent = getParent(ParentId);


        if(ParentId != log.getHabit().getParent().getId())
            throw new ApiException("You are not the owner of this habit");

        if(!parent.getChildren().contains(log.getHabit().getChild()))
            throw new ApiException("This is not your child");

        if (!log.getApprovalStatus().equals("PENDING")) {
            throw new RuntimeException("you already checked this habit");
        }

        log.setApprovalStatus(status);
        log.setApprovedAt(LocalDate.now());
        habitLogRepository.save(log);

        // Give points to child if approved
        if ("COMPLETED".equals(status)) {
            Child child = log.getHabit().getChild();
            if (child != null) {
                child.setPoints(child.getPoints() + log.getHabit().getPoints());
                childRepository.save(child);
            }
        }

    }

    public HabitLog getHabitLogById(Integer logId) {
        HabitLog log = habitLogRepository.findHabitLogById(logId);
        if (log == null) {
            throw new ApiException("Log not found");
        }
        return log;
    }

//    public void completeHabitToday(Integer habitId) {
//        Habit habit = habitRepository.findHabitById(habitId);
//        if (habit == null) {
//            throw new ApiException("Habit not found");
//        }
//
//        HabitLog existingLog = habitLogRepository.findByHabitIdAndLoggedDate(habitId, LocalDate.now());
//        if (existingLog != null) {
//            throw new ApiException("Habit already executed today");
//        }
//
//        HabitLog log = new HabitLog();
//        log.setHabit(habit);
//        log.setLoggedDate(LocalDate.now());
//        log.setApprovalStatus("APPROVED");
//        log.setApprovedAt(LocalDate.now());
//        habitLogRepository.save(log);
//
//        Individual individual = habit.getIndividual();
//        individual.setPoints(individual.getPoints() + habit.getPoints());
//        individualRepository.save(individual);
//    }

    //extra

    public Map<String, Integer> IndividualStreakPerHabit(Integer individualId) {
        Individual individual = getIndividual(individualId);

        Map<String, Integer> streakMap = new HashMap<>();

        //loob through all person habits
        for (Habit habit : individual.getHabits()) {

            //get all approved logs for this habit to calculate streak
            List<HabitLog> approvedLogs = habitLogRepository.findByHabitAndApprovalStatus(habit, "APPROVED");

            // saves distinct dates
            List<LocalDate> dates = new ArrayList<>();
            for (HabitLog log : approvedLogs) {
                LocalDate loggedDate = log.getLoggedDate();
                if (!dates.contains(loggedDate)) {
                    dates.add(loggedDate);
                }
            }

            // Sort dates in reverse order (newest first)
            dates.sort(Comparator.reverseOrder());

            // Calculate streak
            int streak = 0;
            if (!dates.isEmpty()) {
                streak = 1;
                int allowedGap = getAllowedGap(habit.getFrequency());
                for (int i = 0; i < dates.size() - 1; i++) {
                    //check if the gap between dates is within the allowed gap
                    long daysBetween = ChronoUnit.DAYS.between(dates.get(i + 1), dates.get(i));
                    //if so increase streak
                    if (daysBetween <= allowedGap) streak++;
                    else break;
                }
            }
            streakMap.put(habit.getTitle(), streak);
        }
        if(streakMap.isEmpty())
            throw new ApiException("No habits found");

        return streakMap;
    }

    //helpers

    public Habit getHabitById(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if(habit==null)
            throw new RuntimeException("Habit not found");
        return habit;
    }

    public HabitLog isAlreadyLoggedInPeriod(Habit habit) {
        LocalDate now   = LocalDate.now();
        LocalDate start = getPeriodStart(now, habit.getFrequency());
        return habitLogRepository.findHabitLogByHabitIdAndLoggedDateBetweenAndApprovalStatus(habit.getId(), start, now, "APPROVED");
    }

    public LocalDate getPeriodStart(LocalDate date, String frequency) {
         switch (frequency) {
            case "DAILY"   :return date;
            case "WEEKLY"  :return date.with(DayOfWeek.SUNDAY);
            case "MONTHLY" :return date.withDayOfMonth(1);
            default        :return date;
        }
    }

    private LocalDate getPeriodEnd(LocalDate date, String frequency) {
         switch (frequency) {
             case "DAILY"  : return date;
             case "WEEKLY" : return date.with(DayOfWeek.SUNDAY);
             case "MONTHLY" :return date.withDayOfMonth(date.lengthOfMonth());
             default : return date;
        }
    }

    public Individual getIndividual(Integer individualId) {
        Individual individual = individualRepository.findIndividualById(individualId);
        if (individual == null)
            throw new ApiException("Individual not found");
        return individual;
    }

    public Parent getParent(Integer parentId) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null)
            throw new ApiException("Parent not found");
        return parent;
    }

    public Child getChild(Integer childId) {
        Child child = childRepository.findChildById(childId);
        if (child == null)
            throw new ApiException("Child not found");
        return child;
    }

    private int getAllowedGap(String frequency) {
        switch (frequency.toUpperCase()) {
            case "WEEKLY":
                return 7;
            case "MONTHLY":
                return 30;
            case "YEARLY":
                return 365;
            default:
                return 1; // daily
        }
    }
}
