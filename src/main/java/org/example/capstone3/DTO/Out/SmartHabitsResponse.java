package org.example.capstone3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartHabitsResponse {
    private List<SmartHabit> smartHabits;
    // getters & setters
}
