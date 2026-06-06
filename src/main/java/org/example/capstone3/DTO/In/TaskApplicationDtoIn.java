package org.example.capstone3.DTO.In;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskApplicationDtoIn {

    @NotNull(message = "Task ID is required")
    private Integer taskId;

    @NotNull(message = "Child ID is required")
    private Integer childId;

}
