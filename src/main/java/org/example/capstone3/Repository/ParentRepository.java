package org.example.capstone3.Repository;

import org.example.capstone3.Models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Integer> {
    Parent findParentById(Integer id);
    Parent findParentByEmail(String email);
}

