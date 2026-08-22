package com.example.portfolio.repository;

import com.example.portfolio.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByOrderByCategoryAscSortOrderAsc();
    List<Skill> findByVisibleTrueOrderByCategoryAscSortOrderAsc();

    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.visible = true ORDER BY s.category")
    List<String> findDistinctVisibleCategories();
}
