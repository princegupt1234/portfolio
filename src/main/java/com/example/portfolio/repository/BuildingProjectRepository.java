package com.example.portfolio.repository;

import com.example.portfolio.entity.BuildingProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingProjectRepository extends JpaRepository<BuildingProject, Long> {
    List<BuildingProject> findAllByOrderBySortOrderAsc();
}
