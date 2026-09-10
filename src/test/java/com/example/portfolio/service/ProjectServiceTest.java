package com.example.portfolio.service;

import com.example.portfolio.entity.Project;
import com.example.portfolio.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void updatesFeaturedToTrue() {
        Project project = new Project();
        project.setId(1L);
        project.setFeatured(false);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        Project updated = projectService.updateFeatured(1L, true);

        assertEquals(true, updated.getFeatured());
        verify(projectRepository).save(project);
        verify(dataVersionService).bump();
    }

    @Test
    void updatesFeaturedToFalse() {
        Project project = new Project();
        project.setId(1L);
        project.setFeatured(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        Project updated = projectService.updateFeatured(1L, false);

        assertEquals(false, updated.getFeatured());
        verify(projectRepository).save(project);
        verify(dataVersionService).bump();
    }

    @Test
    void rejectsUnknownProject() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.updateFeatured(99L, true));
    }
}
