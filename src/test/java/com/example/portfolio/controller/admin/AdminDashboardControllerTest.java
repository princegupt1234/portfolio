package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ResumeRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.service.AnalyticsService;
import com.example.portfolio.service.GithubStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private GithubStatsService githubStatsService;

    @Mock
    private AiTrainingRepository aiTrainingRepository;

    @InjectMocks
    private AdminDashboardController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersDashboardWithAiTrainingMetrics() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setFullName("Prince Gupt");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));
        when(projectRepository.count()).thenReturn(5L);
        when(skillRepository.count()).thenReturn(12L);
        when(certificateRepository.count()).thenReturn(4L);
        when(contactMessageRepository.countByIsReadFalse()).thenReturn(2L);
        when(contactMessageRepository.count()).thenReturn(10L);
        when(resumeRepository.count()).thenReturn(1L);
        when(analyticsService.last30Days()).thenReturn(Collections.emptyList());
        when(aiTrainingRepository.count()).thenReturn(7L);
        when(aiTrainingRepository.findByActiveTrueOrderBySortOrderAscIdDesc())
                .thenReturn(List.of(new AiTraining("Q1", "A1", "Hiring", "kw1"), new AiTraining("Q2", "A2", "Skills", "kw2")));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("aiTrainingCount", 7L))
                .andExpect(model().attribute("activeAiTrainingCount", 2))
                .andExpect(model().attribute("projectCount", 5L));
    }
}
