package org.example.capstone3.Repository;


import org.example.capstone3.Models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Integer> {
    Habit findHabitById(Integer id);
    List<Habit> findByIndividualId(Integer individualId);

    List<Habit> findHabitsByCategory_Id(Integer categoryId);


    List<Habit> findHabitByIsAiSuggestedFalseAndIndividualId(Integer individualId);

    List<Habit> findHabitByIsAiSuggestedTrueAndIndividualId(Integer individualId);


    List<Habit> findByParentId(Integer parentId);
    List<Habit> findByChildId(Integer childId);
}
