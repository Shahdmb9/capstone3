package org.example.capstone3.Repository;

import org.example.capstone3.Model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Integer> {

    Reward findRewardById(Integer id);

}
