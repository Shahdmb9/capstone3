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
public class Individual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50)", nullable = false)
    private String fullName;

    @Column(columnDefinition = "varchar(50)", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Column(nullable = false)
    private Integer points = 0;

    @OneToOne(mappedBy = "individual", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private HealthProfile healthProfile;

    @OneToMany(mappedBy = "individual", cascade = CascadeType.ALL)
    private Set<Habit> habits;

    @ManyToMany
    @JsonIgnore
    private Set<Badge> badges;
}
