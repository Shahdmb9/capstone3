package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.Models.TaskApplication;
import org.example.capstone3.Repository.TaskApplicationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskApplicationService {

    private final TaskApplicationRepository taskApplicationRepository;

    //endpoint to create child task application



}
