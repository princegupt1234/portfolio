package com.example.portfolio.repository;

import com.example.portfolio.entity.LearningProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningProjectRepository extends JpaRepository<LearningProject, Long> {
    List<LearningProject> findAllByOrderBySortOrderAsc();
}
