package org.example.capstone3.Service;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.DTO.In.TaskDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Models.Task;
import org.example.capstone3.Repository.ChildRepository;
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

    public List<Task> getAllTask(){
        return taskRepository.findAll();
    }
    public void addTask(Integer parentId,TaskDTOIn taskIn){
        Parent parent=parentService.getParentById(parentId);
        Set<Child> children = new HashSet<>(parent.getChildren());
        Task task = new Task(null , taskIn.getTitle() , taskIn.getDescription(),"PENDING",taskIn.getStartDate() ,taskIn.getEndDate(),null,parent,null,children);
        // 2. Save task first to get an ID

        taskRepository.save(task);
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
