package org.example.capstone3.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(100) not null ")
    private String fullName ;
    @Column(nullable = false ,unique = true)
    private String email ;
    @Column(nullable = false )
    private Integer age ;
    @Column(nullable = false )
    private String password ;
    @Column(columnDefinition = "DATETIME default CURRENT_TIMESTAMP")
    private Date createdAt ;
    @Column(columnDefinition = "int default 0 check (points >= 0)")
    private Integer points  ;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Parent parent;

    @OneToMany(mappedBy = "child",cascade = CascadeType.ALL)
    private Set<Habit> habit;

    @OneToMany(mappedBy = "child",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TaskApplication> taskApplications;

    @ManyToMany(mappedBy = "children")
    private Set<Task> task=new HashSet<>();

    @OneToOne(mappedBy = "child", cascade = CascadeType.ALL)
    private Profile profile;

}
