package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate loggedDate = LocalDate.now();

    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String approvalStatus;

    private LocalDate approvedAt;

    @ManyToOne
    @JoinColumn(name = "habit_id", referencedColumnName = "id")
    @JsonIgnore
    private Habit habit;
}
