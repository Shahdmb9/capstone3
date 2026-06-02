package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double weight;
    private Double height;
    private Integer age;

    @Column(columnDefinition = "text")
    private String medicalConditions;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String mainGoal;

    @OneToOne
    @JoinColumn(name = "individual_id", referencedColumnName = "id")
    @JsonIgnore
    private Individual individual;

    /*@OneToOne
    @JoinColumn(name = "child_id", referencedColumnName = "id")
    @JsonIgnore
    private Child child;*/
}
