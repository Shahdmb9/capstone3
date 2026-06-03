package org.example.capstone3.Service;


import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.TaskRewardDTOIn;
import org.example.capstone3.DTO.Out.TaskRewardDTOOut;
import org.example.capstone3.Models.Task;
import org.example.capstone3.Models.TaskReward;
import org.example.capstone3.Repository.TaskRepository;
import org.example.capstone3.Repository.TaskRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskRewardService {
    private final TaskRewardRepository taskRewardRepository;
    private final TaskRepository taskRepository;

    public List<TaskRewardDTOOut> getAllTaskReward() {
        List<TaskRewardDTOOut> taskRewardDTOOuts = new ArrayList<>();
        for (TaskReward t : taskRewardRepository.findAll()) {
            taskRewardDTOOuts.add(convertToDTO(t));
        }
        return taskRewardDTOOuts;
    }

    public void addTaskReward(Integer taskID, TaskRewardDTOIn taskRewardDTOIn) {
        Task task = taskRepository.findTaskById(taskID);
        if (task == null) {
            throw new ApiException("Task not found");
        }
        if (task.getTaskReward() != null) {
            throw new ApiException("task already has a reward");
        }
        TaskReward taskReward = new TaskReward(taskID, taskRewardDTOIn.getTitle(), taskRewardDTOIn.getDescription(), "IN_PROGRESS", null, task);
        taskRewardRepository.save(taskReward);
    }

    public void updateTaskReward(Integer taskID, TaskRewardDTOIn taskRewardDTOIn) {
        Task task = taskRepository.findTaskById(taskID);
        if (task == null) {
            throw new ApiException("Task not found");
        }
        TaskReward taskReward = taskRewardRepository.findTaskRewardById(taskID);
        if (taskReward == null) {
            throw new ApiException("Task Reward not found");
        }
        taskReward.setTitle(taskRewardDTOIn.getTitle());
        taskReward.setDescription(taskRewardDTOIn.getDescription());
        taskRewardRepository.save(taskReward);
    }

    public void deleteTaskReward(Integer taskID) {
        Task task = taskRepository.findTaskById(taskID);
        if (task == null) {
            throw new ApiException("Task not found");
        }
        TaskReward taskReward = taskRewardRepository.findTaskRewardById(taskID);
        if (taskReward == null) {
            throw new ApiException("Task Reward not found");
        }
        taskRewardRepository.delete(taskReward);
    }

    public TaskRewardDTOOut convertToDTO(TaskReward taskReward) {

        return new TaskRewardDTOOut(taskReward.getId(), taskReward.getTitle(), taskReward.getDescription(), taskReward.getStatus());
    }
}
