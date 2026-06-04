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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(150) not null ")
    private String title;
    private String description;
    @Column(columnDefinition = "varchar(20) not null CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'CANCELLED'))")
    private String status;
    private String type;
    private Date startDate;
    private Date endDate;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private TaskReward taskReward;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")//optional
    @JsonIgnore
    private Parent parent;

    @OneToMany(mappedBy = "task",cascade = CascadeType.ALL)
    private Set<TaskApplication> taskApplications;

    @ManyToMany
    @JoinTable(
            name = "task_child",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    @JsonIgnore
    private Set<Child> children=new HashSet<>();
}
