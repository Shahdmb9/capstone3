package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TaskApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate loggedDate = LocalDate.now();

    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String approvalStatus;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ",insertable = false)
    private LocalDate approvedAt;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ",insertable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    @JsonIgnore
    private Task task;

    @ManyToOne
    @JsonIgnore
    private Child child;

}
