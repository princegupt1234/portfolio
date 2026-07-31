package com.example.portfolio.controller;

import com.example.portfolio.dto.ContactForm;
import com.example.portfolio.entity.*;
import com.example.portfolio.repository.*;
import com.example.portfolio.service.AnalyticsService;
import com.example.portfolio.service.GithubStatsService;
import com.example.portfolio.service.LeetCodeStatsService;
import com.example.portfolio.service.MailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Serves the single-page public portfolio and handles the contact form / resume download.
 */
@Controller
public class HomeController {

    private final AboutInfoRepository aboutInfoRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final TestimonialRepository testimonialRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ResumeRepository resumeRepository;
    private final AnalyticsService analyticsService;
    private final GithubStatsService githubStatsService;
    private final LeetCodeStatsService leetCodeStatsService;
    private final MailService mailService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public HomeController(AboutInfoRepository aboutInfoRepository, EducationEntryRepository educationEntryRepository,
                           SkillRepository skillRepository, ExperienceRepository experienceRepository,
                           ProjectRepository projectRepository, CertificateRepository certificateRepository,
                           ServiceItemRepository serviceItemRepository, TestimonialRepository testimonialRepository,
                           ContactMessageRepository contactMessageRepository, ResumeRepository resumeRepository,
                           AnalyticsService analyticsService, GithubStatsService githubStatsService,
                           LeetCodeStatsService leetCodeStatsService, MailService mailService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.resumeRepository = resumeRepository;
        this.analyticsService = analyticsService;
        this.githubStatsService = githubStatsService;
        this.leetCodeStatsService = leetCodeStatsService;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "contactSuccess", required = false) Boolean contactSuccess,
                       Model model) {
        return renderHome(contactSuccess, model);
    }

    public String home(Model model) {
        return renderHome(null, model);
    }

    private String renderHome(Boolean contactSuccess, Model model) {
        analyticsService.recordPortfolioView();

        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<EducationEntry> education = educationEntryRepository.findAllByOrderBySortOrderAsc();
        List<Skill> skills = skillRepository.findAllByOrderByCategoryAscSortOrderAsc();
        List<Experience> experiences = experienceRepository.findAllByOrderBySortOrderAsc();
        List<Project> projects = projectRepository.findAllByOrderBySortOrderAsc();
        List<Certificate> certificates = certificateRepository.findAllByOrderBySortOrderAsc();
        List<ServiceItem> services = serviceItemRepository.findAllByOrderBySortOrderAsc();
        List<Testimonial> testimonials = testimonialRepository.findByPublishedTrue();

        model.addAttribute("about", about);
        model.addAttribute("education", education);
        model.addAttribute("skills", skills);
        model.addAttribute("experiences", experiences);
        model.addAttribute("projects", projects);
        model.addAttribute("certificates", certificates);
        model.addAttribute("services", services);
        model.addAttribute("testimonials", testimonials);
        model.addAttribute("github", githubStatsService.fetchStats(about.getGithubUsername()));
        model.addAttribute("leetcode", leetCodeStatsService.fetchStats(about.getLeetcodeUsername()));

        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactForm());
        }
        if (Boolean.TRUE.equals(contactSuccess)) {
            model.addAttribute("contactSuccess", true);
        }

        return "index";
    }

    @PostMapping("/contact/submit")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactForm form,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return home(model);
        }
        ContactMessage msg = new ContactMessage();
        msg.setName(form.getName());
        msg.setEmail(form.getEmail());
        msg.setSubject(form.getSubject());
        msg.setMessage(form.getMessage());
        contactMessageRepository.save(msg);
        analyticsService.recordMessageReceived();
        mailService.notifyNewMessage(form.getName(), form.getEmail(), form.getSubject(), form.getMessage());

        return "redirect:/?contactSuccess=true";
    }

    @GetMapping("/project/{id}/click")
    @ResponseBody
    public String trackProjectClick(@PathVariable("id") Long id) {
        analyticsService.recordProjectClick();
        return "ok";
    }

    @GetMapping("/resume/download")
    public void downloadResume(HttpServletResponse response) throws IOException {
        Optional<Resume> active = resumeRepository.findByActiveTrue();
        if (active.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No resume uploaded yet");
            return;
        }
        Resume resume = active.get();
        resume.setDownloadCount(resume.getDownloadCount() + 1);
        resumeRepository.save(resume);
        analyticsService.recordResumeDownload();

        String relativePath = resume.getFileUrl().replaceFirst("^/uploads/", "");
        Path filePath = Path.of(uploadDir, relativePath);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Prince_Gupt_Resume.pdf\"");
        Files.copy(filePath, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
