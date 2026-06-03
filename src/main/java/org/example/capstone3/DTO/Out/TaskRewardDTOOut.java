package org.example.capstone3.DTO.Out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRewardDTOOut {
    private Integer id;
    private String title;
    private String description;
    private String status;

}
