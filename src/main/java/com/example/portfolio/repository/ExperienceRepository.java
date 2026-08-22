package com.example.portfolio.repository;

import com.example.portfolio.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findAllByOrderBySortOrderAsc();
    List<Experience> findByVisibleTrueOrderBySortOrderAsc();
}
