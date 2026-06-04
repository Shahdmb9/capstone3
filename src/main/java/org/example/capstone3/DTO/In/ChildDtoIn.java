package org.example.capstone3.DTO.In;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDtoIn {
    @NotEmpty(message = "full Name should not be empty ")
    private String fullName ;
    @NotEmpty(message = "email should not be empty ")
    @Email
    private String email ;
    @NotNull(message = "age should not be empty ")
    @Positive
    private Integer age ;
    @NotEmpty(message = "password should not be empty ")
    @Size(min = 8)
    private String password ;
}
