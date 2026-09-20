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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    @Test
    void testSaveWithBlankFieldsConvertsToNull() throws Exception {
        mockMvc.perform(post("/admin/projects/save")
                        .param("title", "Vistastore")
                        .param("githubUrl", "https://github.com/princegupt1234/Vistastore")
                        .param("liveUrl", "")
                        .param("demoVideoUrl", "   ")
                        .param("engineeringHighlight", "")
                        .param("architectureImageUrl", "")
                        .param("visible", "true")
                        .param("_visible", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"));

        org.mockito.ArgumentCaptor<Project> captor = org.mockito.ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("https://github.com/princegupt1234/Vistastore", saved.getGithubUrl());
        org.junit.jupiter.api.Assertions.assertNull(saved.getLiveUrl(), "Blank liveUrl must be converted to null");
        org.junit.jupiter.api.Assertions.assertNull(saved.getDemoVideoUrl(), "Whitespace demoVideoUrl must be converted to null");
        org.junit.jupiter.api.Assertions.assertNull(saved.getEngineeringHighlight(), "Blank engineeringHighlight must be null");
        org.junit.jupiter.api.Assertions.assertNull(saved.getArchitectureImageUrl(), "Blank architectureImageUrl must be null");
    }

    @Test
    void editForm_projectNotFound_redirectsWithErrorMessage() throws Exception {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/projects/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attribute("errorMessage", "Project not found."));
    }

    @Test
    void editForm_projectFound_rendersForm() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setTitle("Awesome Project");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/admin/projects/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/projects/form"))
                .andExpect(model().attribute("project", project));
    }

    @Test
    void delete_projectNotFound_redirectsWithErrorMessage() throws Exception {
        when(projectRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(post("/admin/projects/999/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attribute("errorMessage", "Project not found."));
    }

    @Test
    void delete_projectFound_deletesAndBumpsVersion() throws Exception {
        when(projectRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/admin/projects/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attribute("successMessage", "Project deleted successfully."));

        verify(projectRepository).deleteById(1L);
        verify(dataVersionService).bump();
    }
}
