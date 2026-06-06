package org.example.capstone3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor

public class TodayHabitDTO {
    private Integer habitId;
    private Integer habitLog;
    private String title;
    private String description;
    private Integer points;
    private Integer streak;
    private Integer highestStreak;
    private String todayStatus;  // NOT_STARTED | PENDING | COMPLETED | REJECTED
}
