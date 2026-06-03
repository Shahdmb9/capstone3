package org.example.capstone3.DTO.In;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTOIn {
    @NotEmpty(message = "Title is required")
    @Size(max = 30)
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(min = 10, max = 255)
    private String description;

    @NotNull(message = "Required points cannot be null")
    @Min(1)
    private Integer requiredPoints;

    @NotNull(message = "Parent ID is required")
    private Long parentId;

    @NotNull(message = "Habit ID is required")
    private Integer habitId;

}
