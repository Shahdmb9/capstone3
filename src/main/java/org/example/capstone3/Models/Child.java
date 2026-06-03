package org.example.capstone3.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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
    @Column(columnDefinition = "int default 0 check (point >= 0)")
    private Integer point  ;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Parent parent;
}
