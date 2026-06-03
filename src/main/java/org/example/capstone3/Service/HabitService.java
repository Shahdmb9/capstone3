package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

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
        Parent parent=getParent(parentId);

        return habitRepository.findByParentId(parentId);
    }

    public List<Habit> getChildHabits(Integer childId) {
        Child child=getChild(childId);
        return habitRepository.findByChildId(childId);
    }

    public void addHabitIndividual(IndividualHabitDTO dto) {
        Individual individual = individualRepository.findIndividualById(dto.getIndividualId());
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
        habit.setPoints(10);
        habit.setIsAiSuggested(false);
        habit.setIndividual(individual);
        habit.setCategory(category);

        habitRepository.save(habit);
    }

    public void addHabitParent(Integer parentId,Integer childId,Habit habit) {

        Parent parent=getParent(parentId);
        Child child=getChild(childId);

        if(!parent.getChildren().contains(child))
            throw new ApiException("This is not your child");

        habit.setParent(parent);
        habit.setChild(child);

        habitRepository.save(habit);
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

    public void completeHabitToday(Integer habitId) {
        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) {
            throw new ApiException("Habit not found");
        }

        HabitLog existingLog = habitLogRepository.findByHabitIdAndLoggedDate(habitId, LocalDate.now());
        if (existingLog != null) {
            throw new ApiException("Habit already executed today");
        }

        HabitLog log = new HabitLog();
        log.setHabit(habit);
        log.setLoggedDate(LocalDate.now());
        log.setApprovalStatus("APPROVED");
        log.setApprovedAt(LocalDate.now());
        habitLogRepository.save(log);

        Individual individual = habit.getIndividual();
        individual.setPoints(individual.getPoints() + habit.getPoints());
        individualRepository.save(individual);
    }

    //helpers
    public Parent getParent(Integer parentId){
        Parent parent=parentRepository.findParentById(parentId);
        if(parent==null)
            throw new ApiException("Parent not found");
        return parent;
    }

    public Child getChild(Integer childId){
        Child child=childRepository.findChildById(childId);
        if(child==null)
            throw new ApiException("Child not found");
        return child;
    }
}
