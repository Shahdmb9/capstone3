package org.example.capstone3.DTO.In;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTOIn {
    @NotEmpty(message = "Category name is required")
    @Size(max = 50)
    private String name;

}
