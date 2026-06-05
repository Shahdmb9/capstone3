package org.example.capstone3.Repository;


import org.example.capstone3.Models.TaskApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskApplicationRepository extends JpaRepository<TaskApplication, Integer> {

    List<TaskApplication> findByTaskIdAndChildId(Integer taskId, Integer childId);
}

