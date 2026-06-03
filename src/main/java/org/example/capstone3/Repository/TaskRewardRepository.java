package org.example.capstone3.Repository;

import org.example.capstone3.Models.TaskReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRewardRepository extends JpaRepository<TaskReward , Integer> {
    TaskReward findTaskRewardById(Integer id);
}
