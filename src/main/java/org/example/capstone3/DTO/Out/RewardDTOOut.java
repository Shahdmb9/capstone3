package org.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTOOut {
    private String title;
    private String description;
    private Integer requiredPoints;
    private String parentName;
    private String habitTitle;

}
