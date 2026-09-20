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
class AdminHiringControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminHiringController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersHiringEditPage() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setAvailabilityText("Immediate");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));

        mockMvc.perform(get("/admin/hiring"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/hiring/edit"))
                .andExpect(model().attributeExists("about"))
                .andExpect(model().attribute("activeSub", "hiring"));
    }

    @Test
    void savesHiringFieldsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/hiring/save")
                        .param("availabilityVisible", "true")
                        .param("availabilityText", "Available for Immediate Joining")
                        .param("hiringRoles", "Software Engineer (SDE-1), Java Developer")
                        .param("hiringNoticePeriod", "Immediate (0 Days)")
                        .param("hiringLocationDetails", "Open to Remote, Hybrid, or On-site")
                        .param("hiringContactEmail", "princegupt3052@gmail.com")
                        .param("hiringCustomNote", "Ready to start immediately.")
                        .param("workPreferencesSectionVisible", "true")
                        .param("workPreference", "Full-time SDE-1")
                        .param("workPreferenceVisible", "true")
                        .param("preferredLocations", "Bangalore, NCR, Remote")
                        .param("preferredLocationsVisible", "true")
                        .param("languagesSpoken", "English, Hindi")
                        .param("languagesSpokenVisible", "true")
                        .param("recruiterPitchEnabled", "true")
                        .param("recruiterTargetRole", "Software Engineer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/hiring"));

        ArgumentCaptor<AboutInfo> captor = ArgumentCaptor.forClass(AboutInfo.class);
        verify(aboutInfoRepository).save(captor.capture());
        AboutInfo saved = captor.getValue();

        assertThat(saved.getAvailabilityVisible()).isTrue();
        assertThat(saved.getAvailabilityText()).isEqualTo("Available for Immediate Joining");
        assertThat(saved.getHiringRoles()).isEqualTo("Software Engineer (SDE-1), Java Developer");
        assertThat(saved.getHiringNoticePeriod()).isEqualTo("Immediate (0 Days)");
        assertThat(saved.getHiringLocationDetails()).isEqualTo("Open to Remote, Hybrid, or On-site");
        assertThat(saved.getHiringContactEmail()).isEqualTo("princegupt3052@gmail.com");
        assertThat(saved.getHiringCustomNote()).isEqualTo("Ready to start immediately.");
        assertThat(saved.getWorkPreferencesSectionVisible()).isTrue();
        assertThat(saved.getRecruiterPitchEnabled()).isTrue();

        verify(dataVersionService).bump();
    }
}
