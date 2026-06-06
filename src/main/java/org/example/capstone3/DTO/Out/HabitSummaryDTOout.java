package org.example.capstone3.DTO.OUT;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HabitSummaryDTOout {

    private Integer habitId;
    private String title;
    private String description;
    private Integer points;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer missedDays;
    private Integer completedDays;
    private String categoryName;
}
