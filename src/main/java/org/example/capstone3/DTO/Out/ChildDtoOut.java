package org.example.capstone3.DTO.Out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDtoOut {
    private Integer id;
    private String fullName ;
    private String email ;
    private Integer age ;
    private Integer point  ;
}
