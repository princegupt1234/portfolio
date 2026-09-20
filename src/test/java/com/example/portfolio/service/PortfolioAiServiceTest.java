package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.AiTraining;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.AiTrainingRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SiteStatRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private LearningProjectRepository learningProjectRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private ServiceItemRepository serviceItemRepository;

    @Mock
    private TestimonialRepository testimonialRepository;

    @Mock
    private SiteStatRepository siteStatRepository;

    @Mock
    private AiTrainingRepository aiTrainingRepository;

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
                learningProjectRepository,
                certificateRepository,
                serviceItemRepository,
                testimonialRepository,
                siteStatRepository,
                aiTrainingRepository,
                leetCodeRepositoryStatsService
        );

        AboutInfo about = new AboutInfo();
        about.setGithubUsername("princegupt1234");
        org.mockito.Mockito.lenient().when(aboutInfoRepository.findAll()).thenReturn(List.of(about));
    }

    @Test
    void testTrainedKnowledgeRuleOverridesDefaultResponse() {
        AiTraining rule = new AiTraining(
                "What is your hourly rate?",
                "Prince is open to contract work at competitive rates starting at $35/hr.",
                "Hiring",
                "hourly, rate, freelance price"
        );
        when(aiTrainingRepository.findByActiveTrueOrderBySortOrderAscIdDesc()).thenReturn(List.of(rule));

        PortfolioAiService.AiAnswerResult res = service.answerWithDetails("What is your hourly rate?");
        assertEquals("Prince is open to contract work at competitive rates starting at $35/hr.", res.reply());
        assertEquals("TRAINED_OVERRIDE", res.source());
    }

    @Test
    void testServicesQueryReturnsServices() {
        ServiceItem item = new ServiceItem();
        item.setTitle("High-Throughput REST APIs");
        item.setDescription("Spring Boot microservice development with clean MySQL indexing.");
        when(serviceItemRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(item));

        String answer = service.answer("what services do you offer?");
        assertTrue(answer.contains("High-Throughput REST APIs"));
        assertTrue(answer.contains("Spring Boot microservice"));
    }

    @Test
    void testTestimonialsQueryReturnsFeedback() {
        Testimonial t = new Testimonial();
        t.setName("Codveda Technologies");
        t.setComment("Prince is an excellent backend engineer with high attention to detail.");
        when(testimonialRepository.findByPublishedTrue()).thenReturn(List.of(t));

        String answer = service.answer("any reviews or testimonials?");
        assertTrue(answer.contains("Codveda Technologies"));
        assertTrue(answer.contains("excellent backend engineer"));
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
