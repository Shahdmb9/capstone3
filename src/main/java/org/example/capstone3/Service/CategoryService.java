package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.CategoryDTOIn;
import org.example.capstone3.DTO.Out.CategoryDTOOut;
import org.example.capstone3.Models.Category;
import org.example.capstone3.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public List<CategoryDTOOut> getAllCategories() {
        List<CategoryDTOOut> dtos = new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {
            CategoryDTOOut dto = modelMapper.map(category, CategoryDTOOut.class);
            dto.setHabitsCount(category.getHabits() != null ? category.getHabits().size() : 0);
            dtos.add(dto);
        }
        return dtos;
    }

    public void addCategory(CategoryDTOIn categoryDTOIn) {
        Category existing = categoryRepository.findCategoryByName(categoryDTOIn.getName());
        if (existing != null) {
            throw new ApiException("Category already exists");
        }

        Category category = modelMapper.map(categoryDTOIn , Category.class);
        categoryRepository.save(category);
    }

    public void updateCategory(Integer id, CategoryDTOIn newCategoryIN) {
        Category oldCategory = categoryRepository.findCategoryById(id);
        if (oldCategory == null) {
            throw new ApiException("Category not found");
        }

        Category existing = categoryRepository.findCategoryByName(newCategoryIN.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new ApiException("Category name already taken by another category");
        }

        oldCategory.setName(newCategoryIN.getName());
        categoryRepository.save(oldCategory);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findCategoryById(id);
        if (category == null) {
            throw new ApiException("Category not found");
        }
        categoryRepository.delete(category);
    }
}
