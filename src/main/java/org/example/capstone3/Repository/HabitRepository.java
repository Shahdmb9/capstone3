package org.example.capstone3.Repository;


import jakarta.transaction.Transactional;
import org.example.capstone3.Models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Integer> {
    Habit findHabitById(Integer id);
    List<Habit> findByIndividualId(Integer individualId);

    List<Habit> findHabitsByCategory_Id(Integer categoryId);

    List<Habit> findByIsAiSuggestedFalse();

    List<Habit> findHabitByIsAiSuggestedFalseAndIndividualId(Integer individualId);

    List<Habit> findHabitByIsAiSuggestedTrueAndIndividualId(Integer individualId);


    @Transactional
    @Modifying
    @Query("Delete from Habit h WHERE h.isAiSuggested = TRUE ")
    Integer deleteAllAiSuggestedTrue();

    List<Habit> findByParentId(Integer parentId);
    List<Habit> findByChildId(Integer childId);
}
