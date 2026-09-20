package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminSectionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminSectionController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersSectionVisibilityPage() throws Exception {
        AboutInfo info = new AboutInfo();
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));

        mockMvc.perform(get("/admin/sections"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sections/index"))
                .andExpect(model().attributeExists("about"))
                .andExpect(model().attribute("activeNav", "sections"));
    }

    @Test
    void savesSectionVisibilitySettings() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/sections/save")
                        .param("sectionHeroVisible", "true")
                        .param("sectionQuickStatsVisible", "false")
                        .param("sectionAboutVisible", "true")
                        .param("sectionSkillsVisible", "true")
                        .param("sectionExperienceVisible", "false")
                        .param("sectionProjectsVisible", "true")
                        .param("sectionCodingVisible", "false")
                        .param("sectionCertificatesVisible", "true")
                        .param("sectionCurrentlyVisible", "false")
                        .param("sectionServicesVisible", "true")
                        .param("sectionTestimonialsVisible", "false")
                        .param("sectionContactVisible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sections"));

        ArgumentCaptor<AboutInfo> captor = ArgumentCaptor.forClass(AboutInfo.class);
        verify(aboutInfoRepository).save(captor.capture());
        AboutInfo saved = captor.getValue();

        assertThat(saved.getSectionHeroVisible()).isTrue();
        assertThat(saved.getSectionQuickStatsVisible()).isFalse();
        assertThat(saved.getSectionAboutVisible()).isTrue();
        assertThat(saved.getSectionSkillsVisible()).isTrue();
        assertThat(saved.getSectionExperienceVisible()).isFalse();
        assertThat(saved.getSectionProjectsVisible()).isTrue();
        assertThat(saved.getSectionCodingVisible()).isFalse();
        assertThat(saved.getSectionCertificatesVisible()).isTrue();
        assertThat(saved.getSectionCurrentlyVisible()).isFalse();
        assertThat(saved.getSectionServicesVisible()).isTrue();
        assertThat(saved.getSectionTestimonialsVisible()).isFalse();
        assertThat(saved.getSectionContactVisible()).isTrue();

        verify(dataVersionService).bump();
    }

    @Test
    void togglesSectionViaAjax() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        existing.setSectionTestimonialsVisible(true);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/sections/toggle")
                        .param("section", "testimonials")
                        .param("visible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.section").value("testimonials"))
                .andExpect(jsonPath("$.visible").value(false));

        ArgumentCaptor<AboutInfo> captor = ArgumentCaptor.forClass(AboutInfo.class);
        verify(aboutInfoRepository).save(captor.capture());
        assertThat(captor.getValue().getSectionTestimonialsVisible()).isFalse();

        verify(dataVersionService).bump();
    }


    @Test
    void returnsBadRequestForInvalidSection() throws Exception {
        AboutInfo existing = new AboutInfo();
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/sections/toggle")
                        .param("section", "nonexistent-section")
                        .param("visible", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
