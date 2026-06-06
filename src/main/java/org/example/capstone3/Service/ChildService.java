package org.example.capstone3.Service;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.DTO.Out.ChildDtoOut;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.Repository.TaskRewardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildService {
    private  final ChildRepository childRepository;
    private  final ParentRepository parentRepository;
    private final TaskRewardRepository taskRewardRepository;

    public List<ChildDtoOut> getAllChildren(){
        List<ChildDtoOut> ChildOuts = new ArrayList<>();
        for (Child c : childRepository.findAll()){
            ChildOuts.add(convertToDTO(c));
        }
        return ChildOuts;
    }
    public void addChild (Integer parentId, ChildDtoIn childIn){
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");
        Child child = new Child(null, childIn.getFullName(), childIn.getEmail(), childIn.getAge(), childIn.getPassword(), new Date(), 0, parent, null, null, null, null);
        childRepository.save(child);
    }
    public void updateChild (Integer id ,ChildDtoIn child ){
        Child oldChild = childRepository.findChildById(id);
        if (oldChild == null) throw new ApiException("Child not found");
        oldChild.setFullName(child.getFullName());
        oldChild.setEmail(child.getEmail());
        oldChild.setAge(child.getAge());
        oldChild.setPassword(child.getPassword());
        childRepository.save(oldChild);
    }
    public void deleteChild(Integer id ) {
        Child child = childRepository.findChildById(id);
        if (child == null) throw new ApiException("Child not found");
        childRepository.delete(child);

    }
    public ChildDtoOut convertToDTO(Child child ){

        return new ChildDtoOut(child.getId() , child.getFullName(), child.getEmail(), child.getAge() ,child.getPoints(),child.getParent().getFullName(),child.getHabit(),child.getTask());
    }
    public List<Reward> getChildClaimedRewards(Integer childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException("Child not found"));

        List<Reward> claimedRewards = new java.util.ArrayList<>();

        if (child.getHabit() != null) {
            for (Habit habit : child.getHabit()) {
                if (habit.getReward() != null && habit.getReward().getClaimedAt() != null) {
                    claimedRewards.add(habit.getReward());
                }
            }
        }
        return claimedRewards;
    }


    public List<TaskReward> getChildClaimedTaskRewards(Integer childId) {
        childRepository.findById(childId)
                .orElseThrow(() -> new ApiException("Child not found"));

        return taskRewardRepository.findByWinnerChildIdAndStatus(childId, "COMPLETED");
    }





}
