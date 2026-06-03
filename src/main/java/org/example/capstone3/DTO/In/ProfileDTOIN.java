package org.example.capstone3.DTO.In;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTOIN {
    @NotNull
    private Integer individualId;

    @Pattern(regexp = "[MF]", message = "Gender must be M or F")
    private Character gender;

    @Positive
    private Double weight;

    @Positive
    private Double height;

    @Min(1) @Max(120)
    private Integer age;

    private String medicalConditions;
    private String badHabit;

    @NotEmpty(message = "Main goal is required")
    private String mainGoal;

}
