package org.example.capstone3.Repository;

import org.example.capstone3.Model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Integer> {

    Parent findParentById(Integer id);
}
