package org.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTOOut {
    private Integer id;
    private Character gender;
    private Double weight;
    private Double height;
    private Integer age;
    private String medicalConditions;
    private String badHabit;
    private String mainGoal;

}
