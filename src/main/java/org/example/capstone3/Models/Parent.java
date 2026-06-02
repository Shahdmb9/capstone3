package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(40)")
    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @Column(columnDefinition = "VARCHAR(20)")
    @Email
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Column(columnDefinition = "VARCHAR(255)")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Habit> habit;

//    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL)
//    private Set<Task> task;

    //in Task class
//    @ManyToOne
//    @JoinColumn(name = "parent_id", referencedColumnName = "id")//optional
//    @JsonIgnore
//    private Parent parent;



    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Reward> rewards;
}