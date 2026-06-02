package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String frequency; // DAILY, WEEKLY, MONTHLY

    @Column(nullable = false)
    private Integer points = 10;

    private Boolean isAiSuggested = false;

    @ManyToOne
    @JoinColumn(name = "individual_id", referencedColumnName = "id")
    @JsonIgnore
    private Individual individual;

   /* @ManyToOne
    @JoinColumn(name = "child_id", referencedColumnName = "id")
    @JsonIgnore
    private Child child;*/

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private Set<HabitLog> logs;
}
