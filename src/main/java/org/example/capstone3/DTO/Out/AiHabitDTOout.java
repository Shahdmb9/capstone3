package org.example.capstone3.DTO.OUT;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiHabitDTOout {
    private Integer id;

    private String title;

    private String description;

    private String frequency;

    private Integer points;
}
