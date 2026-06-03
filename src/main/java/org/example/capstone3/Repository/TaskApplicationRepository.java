package org.example.capstone3.Repository;


import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskApplicationRepository extends JpaRepository<Task, Integer> {
    Task findTasksById(Integer id);
}
