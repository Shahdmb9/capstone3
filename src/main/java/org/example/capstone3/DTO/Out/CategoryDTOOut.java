package org.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTOOut {
    private Integer id;
    private String name;
    private Integer habitsCount;

}
