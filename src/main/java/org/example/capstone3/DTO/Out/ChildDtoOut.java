package org.example.capstone3.DTO.Out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Task;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDtoOut {
    private Integer id;
    private String fullName ;
    private String email ;
    private Integer age ;
    private Integer point;
    private String parentName;
    private Set<Habit> habits;
    private Set<Task> task;
}
