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
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50)", nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Integer pointsRequired;

    @ManyToMany(mappedBy = "badges")
    @JsonIgnore
    private Set<Individual> individuals;
}
