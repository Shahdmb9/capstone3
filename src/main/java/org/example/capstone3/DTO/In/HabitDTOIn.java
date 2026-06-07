package org.example.capstone3.DTO.In;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDTOIn {
    @NotEmpty(message = "Title is required")
    @Size(max = 100)
    private String title;

    private String description;

    @NotEmpty(message = "Frequency is required")
    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY", message = "Frequency must be DAILY, WEEKLY, or MONTHLY")
    private String frequency;

    @Min(1)
    private Integer points = 10;

    private Boolean isAiSuggested = false;


    private Integer individualId;
    private Integer parentId;
    private Integer childId;

    @NotNull(message = "Category is required")
    private Integer categoryId;

}
