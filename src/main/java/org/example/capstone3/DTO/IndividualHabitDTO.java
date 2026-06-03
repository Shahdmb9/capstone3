package org.example.capstone3.DTO;

import lombok.Data;

@Data
public class IndividualHabitDTO {
    private String title;
    private String description;
    private String frequency; // DAILY, WEEKLY
    private Integer categoryId;
    private Integer points;
}
