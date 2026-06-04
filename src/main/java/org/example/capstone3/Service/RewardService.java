package org.example.capstone3.Service;



import lombok.RequiredArgsConstructor;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.RewardDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Reward;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.HabitRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.example.capstone3.Repository.RewardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final ParentRepository parentRepository;
    private final HabitRepository habitRepository;
    private  final ModelMapper modelMapper;
    private final ChildRepository childRepository;

    public List<Reward> getAllRewards(){
        return rewardRepository.findAll();
    }

    public void add(Integer parentId, Integer habitId, RewardDTOIn rewardIn) {
        Parent parent = parentRepository.findParentById(parentId);
        Reward reward = modelMapper.map(rewardIn,Reward.class);
        if (parent == null) throw new ApiException("Parent not found");

        Habit habit = habitRepository.findHabitById(habitId);
        if (habit == null) throw new ApiException("Habit not found");

        if (habit.getReward() != null) {
            throw new ApiException("This habit already has a reward linked to it");
        }

        reward.setParent(parent);
        reward.setHabit(habit);
        reward.setId(habit.getId());

        habit.setReward(reward);
        rewardRepository.save(reward);
    }




    public void update(Integer id,RewardDTOIn rewardIn){
        Reward reward = modelMapper.map(rewardIn,Reward.class);
        Reward oldReward=getRewardById(id);
        if(oldReward==null) throw new ApiException("Reward not found");
        oldReward.setTitle(reward.getTitle());
        oldReward.setDescription(reward.getDescription());
        oldReward.setRequiredPoints(reward.getRequiredPoints());
        rewardRepository.save(oldReward);
    }

    public void delete(Integer id){
        Reward reward=getRewardById(id);
        if(reward==null) throw new ApiException("Reward not found");
        rewardRepository.delete(reward);
    }


    public Reward getRewardById(Integer id){
        Reward reward=rewardRepository.findRewardById(id);
        if(reward==null)
            throw new ApiException("Reward not found");
        return reward;
    }

    public void redeemReward(Integer childId, Integer rewardId) {
        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        Reward reward = rewardRepository.findRewardById(rewardId);
        if (reward == null) throw new ApiException("Reward not found");

        if (child.getPoints() < reward.getRequiredPoints()) {
            throw new ApiException("You do not have enough points for this reward");
        }

        child.setPoints(child.getPoints() - reward.getRequiredPoints());

        childRepository.save(child);
    }


}
