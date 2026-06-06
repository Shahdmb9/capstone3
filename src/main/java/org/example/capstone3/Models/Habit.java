package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @Column(columnDefinition = "text")
    @NotEmpty(message = "Description cannot be empty")
    private String description;


    @Column(columnDefinition = "INT")
    private Integer streak;

    @Column(columnDefinition = "INT")
    private Integer highestStreak;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    @Pattern(regexp = "^(DAILY)$", message = "Frequency must be either DAILY")
    private String frequency;

    @Column(nullable = false)
    @NonNull
    private Integer points = 10;

    private Boolean isAiSuggested = false;

    @ManyToOne
    @JoinColumn(name = "individual_id", referencedColumnName = "id")
    @JsonIgnore
    private Individual individual;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")//optional
    @JsonIgnore
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "child_id", referencedColumnName = "id")
    @JsonIgnore
    private Child child;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private Set<HabitLog> logs;

    @OneToOne(mappedBy = "habit", cascade = CascadeType.ALL)
    private Reward reward;

}
