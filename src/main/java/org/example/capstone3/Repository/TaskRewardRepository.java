package org.example.capstone3.Repository;


import org.example.capstone3.Models.TaskReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRewardRepository extends JpaRepository<TaskReward, Integer> {
    TaskReward findTaskRewardById(Integer id);

    List<TaskReward> findByWinnerChildIdAndStatus(Integer childId, String status);

}

