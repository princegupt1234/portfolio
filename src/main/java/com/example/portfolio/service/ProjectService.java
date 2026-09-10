package com.example.portfolio.service;

import com.example.portfolio.entity.Project;
import com.example.portfolio.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DataVersionService dataVersionService;

    public ProjectService(ProjectRepository projectRepository, DataVersionService dataVersionService) {
        this.projectRepository = projectRepository;
        this.dataVersionService = dataVersionService;
    }

    @Transactional
    public Project updateFeatured(Long id, Boolean featured) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
        project.setFeatured(featured);
        Project updatedProject = projectRepository.save(project);
        dataVersionService.bump();
        return updatedProject;
    }
}
