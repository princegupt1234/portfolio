package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Project;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import com.example.portfolio.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DataVersionService dataVersionService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private AdminProjectController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void updatesFeaturedViaPatch() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setFeatured(true);

        when(projectService.updateFeatured(eq(1L), eq(true))).thenReturn(project);

        mockMvc.perform(patch("/admin/projects/1/featured")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"featured\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.featured").value(true));

        verify(projectService).updateFeatured(1L, true);
    }

    @Test
    void togglesFeaturedViaPostFallback() throws Exception {
        Project project = new Project();
        project.setId(2L);
        project.setFeatured(false);

        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));

        mockMvc.perform(post("/admin/projects/2/toggle-featured"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"));

        verify(projectRepository).save(project);
        verify(dataVersionService).bump();
    }

    @Test
    void testSaveWithStandardSpringCheckboxConvention() throws Exception {
        // When checked: sends field + _field
        mockMvc.perform(post("/admin/projects/save")
                        .param("title", "Test Project")
                        .param("visible", "true")
                        .param("_visible", "on")
                        .param("featured", "true")
                        .param("_featured", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"));

        org.mockito.ArgumentCaptor<Project> captor = org.mockito.ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project saved = captor.getValue();
        System.out.println("DEBUG: standard checked -> visible = " + saved.getVisible() + ", featured = " + saved.getFeatured());
        org.junit.jupiter.api.Assertions.assertTrue(saved.getVisible(), "Visible should be true when checked");
        org.junit.jupiter.api.Assertions.assertTrue(saved.getFeatured(), "Featured should be true when checked");
    }

    @Test
    void testSaveWithStandardSpringCheckboxUnchecked() throws Exception {
        // When unchecked: browser sends ONLY _field
        mockMvc.perform(post("/admin/projects/save")
                        .param("title", "Test Project")
                        .param("_visible", "on")
                        .param("_featured", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"));

        org.mockito.ArgumentCaptor<Project> captor = org.mockito.ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project saved = captor.getValue();
        System.out.println("DEBUG: standard unchecked -> visible = " + saved.getVisible() + ", featured = " + saved.getFeatured());
        org.junit.jupiter.api.Assertions.assertFalse(saved.getVisible(), "Visible should be false when unchecked");
        org.junit.jupiter.api.Assertions.assertFalse(saved.getFeatured(), "Featured should be false when unchecked");
    }
}
