package org.example.capstone3.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.CategoryDTOIn;
import org.example.capstone3.DTO.Out.CategoryDTOOut;
import org.example.capstone3.Service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity<List<CategoryDTOOut>> getAllCategories() {
        return ResponseEntity.status(200).body(categoryService.getAllCategories());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody @Valid CategoryDTOIn category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(201).body(new ApiResponse("New Category added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Integer id, @RequestBody @Valid CategoryDTOIn category) {
        categoryService.updateCategory(id, category);
        return ResponseEntity.status(200).body(new ApiResponse("Category updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(200).body(new ApiResponse("Category deleted successfully"));
    }
}
