package org.example.capstone3.Service;



import lombok.RequiredArgsConstructor;

import org.example.capstone3.Api.ApiException;
import org.example.capstone3.Model.Parent;
import org.example.capstone3.Model.Reward;
import org.example.capstone3.Repository.ParentRepository;
import org.example.capstone3.Repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final ParentRepository parentRepository;

    public List<Reward> getAllRewards(){
        return rewardRepository.findAll();
    }

    public void add(Integer parentId,Reward reward){
        Parent parent=parentRepository.findParentById(parentId);
        if(parent==null)
            throw new ApiException("Parent not found");


        reward.setParent(parent);
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
