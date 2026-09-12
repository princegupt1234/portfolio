package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminHeroControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminHeroController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersHeroEditPage() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setFullName("Prince Gupt");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));

        mockMvc.perform(get("/admin/hero"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/hero/edit"))
                .andExpect(model().attributeExists("about"))
                .andExpect(model().attribute("activeSub", "hero"));
    }

    @Test
    void savesHeroFieldsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        existing.setCareerObjective("Existing Story");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/hero/save")
                        .param("fullName", "Prince Gupt Updated")
                        .param("title", "Java Engineer")
                        .param("heroEyebrow", "// Hello World")
                        .param("quickStatsVisible", "true")
                        .param("quickStats", "10+::Projects::fa-code"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/hero"));

        verify(aboutInfoRepository).save(any(AboutInfo.class));
        verify(dataVersionService).bump();
    }
}
