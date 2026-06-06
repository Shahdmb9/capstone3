package org.example.capstone3.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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

    @Column(columnDefinition = "varchar(50)",nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(20)",nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "int default 0",nullable = false)
    private Integer points = 0;

    @OneToOne(mappedBy = "individual", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "individual", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Habit> habits;

    @ManyToMany
    @JoinTable(
            name = "individual_badges",
            joinColumns = @JoinColumn(name = "individual_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    @JsonIgnore
    private Set<Badge> badges = new HashSet<>();


    @ManyToMany(mappedBy = "individual",cascade = CascadeType.ALL)
    private Set<Category> categories;
}
