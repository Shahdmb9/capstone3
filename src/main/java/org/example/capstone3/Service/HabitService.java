package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.IndividualHabitDTO;
import org.example.capstone3.Models.Category;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.HabitLog;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Repository.CategoryRepository;
import org.example.capstone3.Repository.HabitLogRepository;
import org.example.capstone3.Repository.HabitRepository;
import org.example.capstone3.Repository.IndividualRepository;
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

    public List<Habit> getIndividualHabits(Integer individualId) {
        return habitRepository.findByIndividualId(individualId);
    }

    public void addHabit(IndividualHabitDTO dto) {
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
}
