package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioAiServiceTest {

    @Mock
    private AboutInfoRepository aboutInfoRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private EducationEntryRepository educationEntryRepository;

    @Mock
    private BuildingProjectRepository buildingProjectRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private LeetCodeRepositoryStatsService leetCodeRepositoryStatsService;

    private PortfolioAiService service;

    @BeforeEach
    void setUp() {
        service = new PortfolioAiService(
                aboutInfoRepository,
                skillRepository,
                projectRepository,
                experienceRepository,
                educationEntryRepository,
                buildingProjectRepository,
                certificateRepository,
                leetCodeRepositoryStatsService
        );

        AboutInfo about = new AboutInfo();
        about.setGithubUsername("princegupt1234");
        when(aboutInfoRepository.findAll()).thenReturn(List.of(about));
    }

    @Test
    void testDsaQueryReturnsLiveLeetCodeStatsAndNoStale350Plus() {
        when(leetCodeRepositoryStatsService.fetchStats(anyString()))
                .thenReturn(new LeetCodeRepositoryStatsService.LeetCodeRepositoryStats(7, 5, 2, 0, true));

        String answerDsa = service.answer("dsa questions");
        assertTrue(answerDsa.contains("7 problems"));
        assertTrue(answerDsa.contains("5 Easy"));
        assertTrue(answerDsa.contains("2 Medium"));
        assertFalse(answerDsa.contains("350+"));

        String answerLeetCode = service.answer("leetcode");
        assertTrue(answerLeetCode.contains("7 problems"));
        assertTrue(answerLeetCode.contains("5 Easy"));
        assertTrue(answerLeetCode.contains("2 Medium"));
        assertFalse(answerLeetCode.contains("350+"));
    }

    @Test
    void testCertificationsQueryReturnsCertificates() {
        Certificate c = new Certificate();
        c.setTitle("Java Full Stack Certified");
        c.setOrganization("Oracle / Udemy");
        when(certificateRepository.findByVisibleTrueOrderBySortOrderAsc()).thenReturn(List.of(c));

        String answer = service.answer("show me your certifications");
        assertTrue(answer.contains("Java Full Stack Certified"));
        assertTrue(answer.contains("Oracle / Udemy"));
    }
}
