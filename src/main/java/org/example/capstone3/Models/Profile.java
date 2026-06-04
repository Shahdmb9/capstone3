package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(1)")
    private Character gender;

    private Double weight;
    private Double height;
    private Integer age;

    @Column(columnDefinition = "text")
    private String medicalConditions;

    @Column(columnDefinition = "text")
    private String badHabit;

    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String mainGoal;

    @OneToOne
    @MapsId
    @JoinColumn(name = "individual_id")
    @JsonIgnore
    private Individual individual;

    @OneToOne
    @JoinColumn(name = "child_id", referencedColumnName = "id")
    @JsonIgnore
    private Child child;
}
