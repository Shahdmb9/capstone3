package org.example.capstone3.DTO;

import lombok.Data;

@Data
public class ChildHealthProfileDTO {
    private Double weight;
    private Double height;
    private Integer age;
    private String medicalConditions;
    private String mainGoal;
    private Integer childId;
}
