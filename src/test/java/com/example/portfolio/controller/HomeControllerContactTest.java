package com.example.portfolio.controller;

import com.example.portfolio.dto.ContactForm;
import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ResumeRepository;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.repository.TestimonialRepository;
import com.example.portfolio.service.AnalyticsService;
import com.example.portfolio.service.ContactRateLimiterService;
import com.example.portfolio.service.ContactReferenceIdGenerator;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.GithubStatsService;
import com.example.portfolio.service.LeetCodeRepositoryStatsService;
import com.example.portfolio.service.MailService;
import com.example.portfolio.service.VisitorAnalyticsService;
import com.example.portfolio.service.WhatsAppNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeControllerContactTest {

    @Mock private AboutInfoRepository aboutInfoRepository;
    @Mock private EducationEntryRepository educationEntryRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private ExperienceRepository experienceRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private ServiceItemRepository serviceItemRepository;
    @Mock private TestimonialRepository testimonialRepository;
    @Mock private BuildingProjectRepository buildingProjectRepository;
    @Mock private LearningProjectRepository learningProjectRepository;
    @Mock private ContactMessageRepository contactMessageRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private AnalyticsService analyticsService;
    @Mock private GithubStatsService githubStatsService;
    @Mock private LeetCodeRepositoryStatsService leetCodeRepositoryStatsService;
    @Mock private MailService mailService;
    @Mock private DataVersionService dataVersionService;
    @Mock private ContactRateLimiterService contactRateLimiterService;
    @Mock private ContactReferenceIdGenerator contactReferenceIdGenerator;
    @Mock private VisitorAnalyticsService visitorAnalyticsService;
    @Mock private WhatsAppNotificationService whatsAppNotificationService;

    @InjectMocks
    private HomeController controller;

    @Test
    void successfulSubmissionPersistsNewMessageAndFlashesOpaqueReference() {
        when(contactReferenceIdGenerator.generate()).thenReturn("00112233445566778899aabbccddeeff");
        AboutInfo about = new AboutInfo();
        about.setContactSpamProtectionEnabled(false);
        when(aboutInfoRepository.findAll()).thenReturn(List.of(about));
        when(contactMessageRepository.existsByReferenceId(any())).thenReturn(false);

        ContactForm form = validForm();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.submitContact(form, bindingResult(form),
                new MockHttpServletRequest(), new MockHttpServletResponse(),
                new ExtendedModelMap(), redirectAttributes);

        assertEquals("redirect:/#contact", result);
        assertEquals("00112233445566778899aabbccddeeff",
                redirectAttributes.getFlashAttributes().get("contactReference"));
        org.mockito.ArgumentCaptor<ContactMessage> message =
                org.mockito.ArgumentCaptor.forClass(ContactMessage.class);
        verify(contactMessageRepository).save(message.capture());
        assertEquals("00112233445566778899aabbccddeeff", message.getValue().getReferenceId());
        assertEquals("NEW", message.getValue().getStatus().name());
    }

    @Test
    void invalidSubmissionDoesNotSaveOrSetSuccessReference() {
        ContactForm form = validForm();
        BeanPropertyBindingResult errors = bindingResult(form);
        errors.rejectValue("email", "invalid", "Enter a valid email");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addUserRole("ADMIN");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        stubHomeRendering();

        String result = controller.submitContact(form, errors, request, new MockHttpServletResponse(),
                new ExtendedModelMap(), redirectAttributes);

        assertEquals("index", result);
        assertFalse(redirectAttributes.getFlashAttributes().containsKey("contactReference"));
        verify(contactMessageRepository, never()).save(any(ContactMessage.class));
    }

    @Test
    void honeypotSubmissionDoesNotShowSuccessOrPersistMessage() {
        when(aboutInfoRepository.findAll()).thenReturn(List.of(new AboutInfo()));
        ContactForm form = validForm();
        form.setWebsiteTrap("automated submission");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.submitContact(form, bindingResult(form),
                new MockHttpServletRequest(), new MockHttpServletResponse(),
                new ExtendedModelMap(), redirectAttributes);

        assertEquals("redirect:/#contact", result);
        assertFalse(redirectAttributes.getFlashAttributes().containsKey("contactReference"));
        verify(contactMessageRepository, never()).save(any(ContactMessage.class));
    }

    private ContactForm validForm() {
        ContactForm form = new ContactForm();
        form.setName("Visitor");
        form.setEmail("visitor@example.com");
        form.setSubject("Portfolio");
        form.setMessage("Hello");
        return form;
    }

    private BeanPropertyBindingResult bindingResult(ContactForm form) {
        return new BeanPropertyBindingResult(form, "contactForm");
    }

    private void stubHomeRendering() {
        when(aboutInfoRepository.findAll()).thenReturn(List.of(new AboutInfo()));
        when(educationEntryRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());
        when(skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc()).thenReturn(List.of());
        when(skillRepository.findDistinctVisibleCategories()).thenReturn(List.of());
        when(experienceRepository.findByVisibleTrueOrderBySortOrderAsc()).thenReturn(List.of());
        when(projectRepository.findByVisibleTrueOrderBySortOrderAsc()).thenReturn(List.of());
        when(certificateRepository.findByVisibleTrueOrderBySortOrderAsc()).thenReturn(List.of());
        when(serviceItemRepository.findByVisibleTrueOrderBySortOrderAsc()).thenReturn(List.of());
        when(testimonialRepository.findByPublishedTrue()).thenReturn(List.of());
        when(buildingProjectRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());
        when(learningProjectRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of());
    }
}
