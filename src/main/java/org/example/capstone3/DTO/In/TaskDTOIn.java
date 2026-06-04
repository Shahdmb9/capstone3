package org.example.capstone3.DTO.In;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOIn {
    @NotEmpty(message = "title should not be empty ")
    private String title;
    private String description;
    @NotEmpty(message = "type should not be empty ")

    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|ONE_TIME|CHALLENGE", message = "type must be DAILY, WEEKLY, MONTHLY, ONE_TIME, or CHALLENGE")
    private String type;
    private Date startDate ;
    private Date endDate ;
}
