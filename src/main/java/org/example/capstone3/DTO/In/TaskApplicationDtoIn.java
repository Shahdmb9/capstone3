package org.example.capstone3.DTO.In;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Task;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskApplicationDtoIn {

   @NotNull(message = "logged Date should not be empty ")
    private LocalDate loggedDate = LocalDate.now();

   @NotEmpty(message = "approval Status should not be empty ")
    private String approvalStatus;//check

    @NotNull(message = "approved At should not be empty ")
    private LocalDate approvedAt;

    @NotNull(message = "approved At should not be empty ")
    private LocalDate createdAt;

    private Task task;

    private Child child;

}
