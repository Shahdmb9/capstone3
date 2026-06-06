package org.example.capstone3.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.aspectj.bridge.IMessage;

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
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @Column(columnDefinition = "text")
    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Points required cannot be null")
    private Integer pointsRequired;

    @ManyToMany(mappedBy = "badges")
    private Set<Individual> individuals;
}
