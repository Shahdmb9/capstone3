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
public class BadgeDTOin {
    @NotEmpty(message = "Title is required")
    @Size(max = 50)
    private String title;

    private String description;

    @NotNull(message = "Points required cannot be null")
    @Min(1)
    private Integer pointsRequired;

}
