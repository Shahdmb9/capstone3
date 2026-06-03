package org.example.capstone3.Service;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.DTO.In.TaskDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Task;
import org.example.capstone3.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private  final TaskRepository taskRepository;
    public List<Task> getAllTask(){
        return taskRepository.findAll();
    }
    public void addTask(TaskDTOIn taskIn){
        Task task = new Task(null , taskIn.getTitle() , taskIn.getDescription(),taskIn.getType(),"PENDING",taskIn.getStartDate() ,taskIn.getEndDate(),null,null);
        taskRepository.save(task);
    }
    public void updateTask( Integer id,TaskDTOIn taskIn){
        Task task = taskRepository.findTaskById(id);
        if (task == null) throw new ApiException("task not found");
        task.setTitle(taskIn.getTitle());
        task.setDescription(taskIn.getDescription());
        task.setType(taskIn.getType());
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
