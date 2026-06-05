package org.example.capstone3.Repository;

import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Integer> {
    HabitLog findByHabitIdAndLoggedDate(Integer habitId, LocalDate loggedDate);

    HabitLog findHabitLogById(Integer id);

    List<HabitLog> findHabitLogByHabitIdAndLoggedDateBetweenAndApprovalStatus(Integer habitId, LocalDate start, LocalDate end, String status);

    HabitLog findHabitLogsByCreatedAt(LocalDate date);

    HabitLog findHabitLogByHabit(Habit habit);

    List<HabitLog> findByHabitAndApprovalStatusOrderByLoggedDateDesc(Habit habit, String approvalStatus);


    List<HabitLog> findByHabitAndApprovalStatus(Habit habit, String approvalStatus);
}

