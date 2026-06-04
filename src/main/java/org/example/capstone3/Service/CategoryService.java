package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.CategoryDTOIn;
import org.example.capstone3.Models.Category;
import org.example.capstone3.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public final ModelMapper modelMapper;
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void addCategory(CategoryDTOIn categoryDTOIn) {
        Category category = modelMapper.map(categoryDTOIn , Category.class);
        Category existing = categoryRepository.findCategoryById(category.getId());
        if (existing != null) {
            throw new ApiException("Category already exists");
        }
        categoryRepository.save(category);
    }

    public void updateCategory(Integer id, CategoryDTOIn newCategoryIN) {
        Category oldCategory = categoryRepository.findCategoryById(id);
        Category newCategory = modelMapper.map(newCategoryIN , Category.class);
        if (oldCategory == null) {
            throw new ApiException("Category not found");
        }
        oldCategory.setName(newCategory.getName());
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
