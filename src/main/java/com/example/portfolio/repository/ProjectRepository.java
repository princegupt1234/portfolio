package com.example.portfolio.repository;

import com.example.portfolio.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderBySortOrderAsc();
    List<Project> findByVisibleTrueOrderBySortOrderAsc();
    List<Project> findByVisibleTrueAndFeaturedTrueOrderBySortOrderAsc();

    @Query("select p from Project p where p.visible = true and (p.featured = false or p.featured is null) order by p.sortOrder asc")
    List<Project> findVisibleNonFeaturedOrderBySortOrderAsc();
}
