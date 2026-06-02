package org.example.capstone3.Repository;

import org.example.capstone3.Models.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Integer> {
    HabitLog findByHabitIdAndLoggedDate(Integer habitId, LocalDate loggedDate);
}

