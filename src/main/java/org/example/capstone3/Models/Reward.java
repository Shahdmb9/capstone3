package org.example.capstone3.Models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reward {

    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(30) ",nullable = false)
    private String title;

    @Column(columnDefinition = "text",nullable = false)
    @NotEmpty(message = "Description cannot be empty")
    @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
    private String description;

    @Column(columnDefinition = "int",nullable = false)
    @NotNull(message = "Required points cannot be null")
    private Integer requiredPoints;

    @Column(columnDefinition = "TIMESTAMP ", insertable = false)
    private LocalDateTime claimedAt;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Parent parent;

    @OneToOne
    @MapsId
    @JoinColumn(name = "habit_id")
    private Habit habit;


}
