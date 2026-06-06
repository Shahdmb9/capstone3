package org.example.capstone3.Service;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.DTO.In.TaskDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Task;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.example.capstone3.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    private  final TaskRepository taskRepository;
    private final ParentService parentService;
    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;

    public List<Task> getAllTask(){
        return taskRepository.findAll();
    }
    public void addTask(Integer parentId, TaskDTOIn taskIn) {
        Parent parent = parentRepository.findParentById(parentId);
        if (parent == null) throw new ApiException("Parent not found");

        Set<Child> children = new HashSet<>(parent.getChildren());

        Task task = new Task();
        task.setTitle(taskIn.getTitle());
        task.setDescription(taskIn.getDescription());
        task.setStatus("PENDING");
        task.setStartDate(taskIn.getStartDate());
        task.setEndDate(taskIn.getEndDate());
        task.setParent(parent);
        task.setChildren(children);

        Task savedTask = taskRepository.save(task);

        for (Child child : children) {
            child.getTask().add(savedTask);
            childRepository.save(child);
        }
    }

    public void updateTask( Integer id,TaskDTOIn taskIn){
        Task task = taskRepository.findTaskById(id);
        if (task == null) throw new ApiException("task not found");
        task.setTitle(taskIn.getTitle());
        task.setDescription(taskIn.getDescription());
        task.setStartDate(taskIn.getStartDate());
        task.setEndDate(taskIn.getEndDate());
        taskRepository.save(task);
    }
    public void deleteTask(Integer id ) {
        Task task = taskRepository.findTaskById(id);
        if (task == null) throw new ApiException("task not found");
       taskRepository.delete(task);

    }


}
