package org.example.capstone3.Service;

import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ChildDtoIn;
import org.example.capstone3.DTO.Out.ChildDtoOut;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildService {
    private  final ChildRepository childRepository;
    private  final ParentRepository parentRepository;
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
        Child child = new Child(null,childIn.getFullName(),childIn.getEmail(),childIn.getAge() , childIn.getPassword(),new Date(),0 ,parent,null,null,null);
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

        return new ChildDtoOut(child.getId() , child.getFullName(), child.getEmail(), child.getAge() ,child.getPoint(),child.getParent(),child.getHabit(),null);
    }
}
