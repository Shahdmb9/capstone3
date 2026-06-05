package org.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOIn {

    @NotEmpty(message = "Title should not be empty")
    private String title;

    private String description;

    @NotNull(message = "Start date should not be null")
    private Date startDate;

    @NotNull(message = "End date should not be null")
    private Date endDate;
}

