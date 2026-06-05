package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ")
    private LocalDate loggedDate = LocalDate.now();

    @Column(columnDefinition = "varchar(20)", nullable = false)
    @NotBlank(message = "Approval status is required")
    @Pattern(regexp = "^(NOT_STARTED|PENDING|COMPLETED|REJECTED)$", message = "Status must be either PENDING, APPROVED, or REJECTED")
    private String approvalStatus;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ",insertable = false)
    private LocalDate approvedAt;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ",insertable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "habit_id", referencedColumnName = "id")
    @JsonIgnore
    private Habit habit;
}
