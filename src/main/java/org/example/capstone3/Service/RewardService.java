package org.example.capstone3.Service;



import lombok.RequiredArgsConstructor;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Reward;
import org.example.capstone3.Repository.HabitRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.example.capstone3.Repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final ParentRepository parentRepository;
    private final HabitRepository habitRepository;

    public List<Reward> getAllRewards(){
        return rewardRepository.findAll();
    }

    public void add(Integer parentId,Integer habitId,Reward reward){
        Parent parent=parentRepository.findParentById(parentId);
        Habit habit=habitRepository.findHabitById(habitId);
        if(habit==null)
            throw new ApiException("Habit not found");

        if(parent==null)
            throw new ApiException("Parent not found");


        reward.setParent(parent);
        reward.setHabit(habit);
        rewardRepository.save(reward);
    }



    public void update(Integer id,Reward reward){
        Reward oldReward=getRewardById(id);
        oldReward.setTitle(reward.getTitle());
        oldReward.setDescription(reward.getDescription());
        oldReward.setRequiredPoints(reward.getRequiredPoints());
        rewardRepository.save(oldReward);
    }

    public void delete(Integer id){
        Reward reward=getRewardById(id);
        rewardRepository.delete(reward);
    }


    public Reward getRewardById(Integer id){
        Reward reward=rewardRepository.findRewardById(id);
        if(reward==null)
            throw new ApiException("Reward not found");
        return reward;
    }

}
