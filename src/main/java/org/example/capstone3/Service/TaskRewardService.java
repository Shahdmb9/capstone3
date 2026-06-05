package org.example.capstone3.Service;


import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.TaskRewardDTOIn;
import org.example.capstone3.DTO.Out.TaskRewardDTOOut;
import org.example.capstone3.Models.Parent;
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
    private final ParentService parentService;

    public List<TaskRewardDTOOut> getAllTaskReward() {
        List<TaskRewardDTOOut> taskRewardDTOOuts = new ArrayList<>();
        for (TaskReward t : taskRewardRepository.findAll()) {
            taskRewardDTOOuts.add(convertToDTO(t));
        }
        return taskRewardDTOOuts;
    }

    public void addTaskReward(Integer parentId, Integer taskID, TaskRewardDTOIn taskRewardDTOIn) {
        Task task = taskRepository.findTaskById(taskID);
        if (task == null) {
            throw new ApiException("Task not found");
        }

        Parent parent = task.getParent();

        if (task.getTaskReward() != null) {
            throw new ApiException("Task already has a reward");
        }

        TaskReward taskReward = new TaskReward();
        taskReward.setTitle(taskRewardDTOIn.getTitle());
        taskReward.setDescription(taskRewardDTOIn.getDescription());
        taskReward.setStatus("IN_PROGRESS");
        taskReward.setParent(parent);

        taskReward.setTask(task);
        task.setTaskReward(taskReward);

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
        // review
        taskRewardRepository.delete(taskReward);
    }

    public TaskRewardDTOOut convertToDTO(TaskReward taskReward) {

        return new TaskRewardDTOOut(taskReward.getId(), taskReward.getTitle(), taskReward.getDescription(), taskReward.getStatus());
    }
}
