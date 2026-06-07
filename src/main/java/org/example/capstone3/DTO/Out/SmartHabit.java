package org.example.capstone3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartHabit {
    private String title;
    private String description;
    private String targetBadHabit;
    private List<ReplacementHabit> replacementHabits;
    // getters & setters
}

