package org.example.capstone3.Repository;

import org.example.capstone3.Models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Integer> {

    Parent findParentById(Integer id);
}
