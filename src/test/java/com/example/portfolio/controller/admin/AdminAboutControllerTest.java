package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.LearningProjectRepository;
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
class AdminAboutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private EducationEntryRepository educationEntryRepository;

    @Mock
    private BuildingProjectRepository buildingProjectRepository;

    @Mock
    private LearningProjectRepository learningProjectRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DataVersionService dataVersionService;

    @InjectMocks
    private AdminAboutController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersAboutEditPage() throws Exception {
        AboutInfo info = new AboutInfo();
        info.setCareerObjective("Passionate Engineer");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(info));
        when(educationEntryRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());
        when(buildingProjectRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());
        when(learningProjectRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());

        mockMvc.perform(get("/admin/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/about/edit"))
                .andExpect(model().attributeExists("about"))
                .andExpect(model().attribute("activeSub", "about"));
    }

    @Test
    void savesAboutFieldsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        existing.setFullName("Prince Gupt");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/about/save")
                        .param("careerObjective", "Updated Journey")
                        .param("location", "Ghaziabad")
                        .param("availabilityVisible", "true")
                        .param("workPreference", "Remote & Hybrid")
                        .param("terminalEnabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/about"));

        verify(aboutInfoRepository).save(any(AboutInfo.class));
        verify(dataVersionService).bump();
    }

    @Test
    void savesQuickStatsFieldsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/about/save")
                        .param("quickStats", "15+::Projects::fa-solid fa-code|400+::DSA::fa-solid fa-laptop")
                        .param("quickStatsVisible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/about"));

        verify(aboutInfoRepository).save(org.mockito.ArgumentMatchers.argThat(info ->
                "15+::Projects::fa-solid fa-code|400+::DSA::fa-solid fa-laptop".equals(info.getQuickStats()) &&
                Boolean.TRUE.equals(info.getQuickStatsVisible())
        ));
        verify(dataVersionService).bump();
    }

    @Test
    void savesWorkPreferencesAndSeparateTogglesAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/about/save")
                        .param("workPreferencesSectionVisible", "true")
                        .param("availabilityVisible", "true")
                        .param("availabilityText", "Actively interviewing")
                        .param("workPreference", "Full-time (Remote)")
                        // workPreferenceVisible is unchecked -> omitted
                        .param("preferredLocations", "Bengaluru, Remote")
                        .param("preferredLocationsVisible", "true")
                        .param("languagesSpoken", "English, Hindi")
                        // languagesSpokenVisible is unchecked -> omitted
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/about"));

        verify(aboutInfoRepository).save(org.mockito.ArgumentMatchers.argThat(info ->
                Boolean.TRUE.equals(info.getWorkPreferencesSectionVisible()) &&
                Boolean.TRUE.equals(info.getAvailabilityVisible()) &&
                Boolean.FALSE.equals(info.getWorkPreferenceVisible()) &&
                Boolean.TRUE.equals(info.getPreferredLocationsVisible()) &&
                Boolean.FALSE.equals(info.getLanguagesSpokenVisible()) &&
                "Actively interviewing".equals(info.getAvailabilityText()) &&
                "Full-time (Remote)".equals(info.getWorkPreference()) &&
                "Bengaluru, Remote".equals(info.getPreferredLocations()) &&
                "English, Hindi".equals(info.getLanguagesSpoken())
        ));
        verify(dataVersionService).bump();
    }

    @Test
    void savesOgSpamProtectionAndPwaSettingsAndRedirects() throws Exception {
        AboutInfo existing = new AboutInfo();
        existing.setId(1L);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(existing));

        mockMvc.perform(post("/admin/about/save")
                        .param("ogTagsEnabled", "true")
                        .param("ogTitle", "Prince Gupt | SDE-1")
                        .param("ogDescription", "High performance Java & Spring Boot developer.")
                        .param("ogImageUrl", "https://portfolio-gx88.onrender.com/images/og-preview.png")
                        .param("contactSpamProtectionEnabled", "true")
                        .param("contactHoneypotEnabled", "true")
                        .param("contactRateLimitSeconds", "45")
                        .param("pwaEnabled", "true")
                        .param("pwaAppName", "Prince Portfolio WebApp")
                        .param("pwaShortName", "Prince App")
                        .param("pwaThemeColor", "#0a0f1d")
                        .param("pwaBackgroundColor", "#060913")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/about"));

        verify(aboutInfoRepository).save(org.mockito.ArgumentMatchers.argThat(info ->
                Boolean.TRUE.equals(info.getOgTagsEnabled()) &&
                "Prince Gupt | SDE-1".equals(info.getOgTitle()) &&
                "High performance Java & Spring Boot developer.".equals(info.getOgDescription()) &&
                "https://portfolio-gx88.onrender.com/images/og-preview.png".equals(info.getOgImageUrl()) &&
                Boolean.TRUE.equals(info.getContactSpamProtectionEnabled()) &&
                Boolean.TRUE.equals(info.getContactHoneypotEnabled()) &&
                Integer.valueOf(45).equals(info.getContactRateLimitSeconds()) &&
                Boolean.TRUE.equals(info.getPwaEnabled()) &&
                "Prince Portfolio WebApp".equals(info.getPwaAppName()) &&
                "Prince App".equals(info.getPwaShortName()) &&
                "#0a0f1d".equals(info.getPwaThemeColor()) &&
                "#060913".equals(info.getPwaBackgroundColor())
        ));
        verify(dataVersionService).bump();
    }
}
