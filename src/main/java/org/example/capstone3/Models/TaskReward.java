package org.example.capstone3.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskReward {
    @Id
    private Integer id;
    @Column(columnDefinition = "varchar(150) not null ")
    private String title;
    private String description;
    @Column(columnDefinition = "varchar(40) not null CHECK (status IN ( 'IN_PROGRESS', 'COMPLETED'))")
    private String status;
    @Column(updatable = false)
    private Date claimedAt;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    @MapsId
    @JsonIgnore
    private Task task;

    @ManyToOne
    @JsonIgnore
    private Parent parent;

}
