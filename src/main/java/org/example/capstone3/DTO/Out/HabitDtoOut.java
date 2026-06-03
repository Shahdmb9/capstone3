package org.example.capstone3.DTO.Out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.capstone3.Models.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HabitDtoOut {

    private Integer id;

    private String title;

    private String description;

    private String frequency;

    private Integer points;

    private Individual individual;

    private Parent parent;

    private Child child;

    private Category category;

}
