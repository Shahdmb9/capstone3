package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.IndividualDTOIn;
import org.example.capstone3.Models.Category;
import org.example.capstone3.Models.Individual;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Repository.CategoryRepository;
import org.example.capstone3.Repository.IndividualRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndividualService {

    private final IndividualRepository individualRepository;
    private final CategoryRepository categoryRepository;
    private  final ModelMapper modelMapper;

    public List<Individual> getAllIndividuals() {
        return individualRepository.findAll();
    }

    public void addIndividual(IndividualDTOIn individualIn) {
        Individual individual =  modelMapper.map(individualIn,Individual.class);
        if (individual != null) {
            throw new ApiException("Email already registered");
        }

        individual.setPoints(0);
        individualRepository.save(individual);
    }

    public void updateIndividual(Integer id, IndividualDTOIn newIndividualIn)
        {
        Individual oldIndividual = individualRepository.findIndividualById(id);
        if (oldIndividual == null) {
            throw new ApiException("Individual not found");
        }
        Individual newIndividual =  modelMapper.map(newIndividualIn,Individual.class);
        oldIndividual.setFullName(newIndividual.getFullName());
        oldIndividual.setPassword(newIndividual.getPassword());
        oldIndividual.setPhoneNumber(newIndividual.getPhoneNumber());
        individualRepository.save(oldIndividual);
    }

    public void deleteIndividual(Integer id) {
        Individual individual = individualRepository.findIndividualById(id);
        if (individual == null) {
            throw new ApiException("Individual not found");
        }
        individualRepository.delete(individual);
    }

    public void addInterest (Integer individualId, Integer categoryId){
        Individual individual = individualRepository.findIndividualById(individualId);
        Category category = categoryRepository.findCategoryById(categoryId);

        if (individual == null)
            throw new ApiException("Individual not found"); // ✅ تصحيح نص الرسالة ليكون دقيقاً
        if (category == null)
            throw new ApiException("Category not found");

        individual.getCategories().add(category);
        category.getIndividual().add(individual);

        individualRepository.save(individual);
    }


}

