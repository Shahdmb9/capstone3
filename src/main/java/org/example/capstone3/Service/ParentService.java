package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Repository.ParentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    public List<Parent> getAllParents(){
        return parentRepository.findAll();
    }

    public void add(Parent parent){
        parent.setCreatedAt(java.time.LocalDateTime.now());
        parentRepository.save(parent);
    }

    public void delete(Integer id){
        Parent parent=parentRepository.findParentById(id);
        if(parent==null)
            throw new ApiException("Parent not found");
        parentRepository.delete(parent);
    }

    public void update(Integer id,Parent parent){
        Parent oldParent=parentRepository.findParentById(id);
        if(oldParent==null)
            throw new ApiException("Parent not found");
        oldParent.setEmail(parent.getEmail());
        oldParent.setFullName(parent.getFullName());
        oldParent.setPassword(parent.getPassword());
        parentRepository.save(oldParent);
    }

}
