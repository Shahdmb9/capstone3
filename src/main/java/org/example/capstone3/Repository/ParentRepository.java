package org.example.capstone3.Repository;

import org.example.capstone3.Models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Integer> {
    @Query("SELECT p FROM Parent p WHERE p.id = ?1")
    Parent findParentById(Integer id);
}

