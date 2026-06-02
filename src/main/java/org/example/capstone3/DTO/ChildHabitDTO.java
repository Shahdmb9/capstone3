package org.example.capstone3.DTO;

import lombok.Data;

@Data
public class ChildHabitDTO {
    private String title;
    private String description;
    private String frequency;
    private Integer points;
    private Integer categoryId;
    private Integer childId;
}
