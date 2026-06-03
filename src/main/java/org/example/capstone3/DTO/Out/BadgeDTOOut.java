package org.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTOOut {
    private Integer id;
    private String title;
    private String description;
    private Integer pointsRequired;
    private Integer earnedByCount;

}
