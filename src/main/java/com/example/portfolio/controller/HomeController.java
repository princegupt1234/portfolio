package com.example.portfolio.controller;

import com.example.portfolio.dto.ContactForm;
import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.Certificate;
import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.Experience;
import com.example.portfolio.entity.Project;
import com.example.portfolio.entity.Resume;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.entity.Skill;
import com.example.portfolio.entity.Testimonial;
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
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.GithubStatsService;
import com.example.portfolio.service.LeetCodeRepositoryStatsService;
import com.example.portfolio.service.MailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ResumeRepository resumeRepository;
    private final AnalyticsService analyticsService;
    private final GithubStatsService githubStatsService;
    private final LeetCodeRepositoryStatsService leetCodeRepositoryStatsService;
    private final MailService mailService;
    private final DataVersionService dataVersionService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public HomeController(AboutInfoRepository aboutInfoRepository, EducationEntryRepository educationEntryRepository,
                           SkillRepository skillRepository, ExperienceRepository experienceRepository,
                           ProjectRepository projectRepository, CertificateRepository certificateRepository,
                           ServiceItemRepository serviceItemRepository, TestimonialRepository testimonialRepository,
                           BuildingProjectRepository buildingProjectRepository,
                           LearningProjectRepository learningProjectRepository,
                           ContactMessageRepository contactMessageRepository, ResumeRepository resumeRepository,
                           AnalyticsService analyticsService, GithubStatsService githubStatsService,
                           LeetCodeRepositoryStatsService leetCodeRepositoryStatsService,
                           MailService mailService, DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.resumeRepository = resumeRepository;
        this.analyticsService = analyticsService;
        this.githubStatsService = githubStatsService;
        this.leetCodeRepositoryStatsService = leetCodeRepositoryStatsService;
        this.mailService = mailService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping("/api/data-version")
    @ResponseBody
    public java.util.Map<String, Long> dataVersion() {
        return java.util.Map.of("version", dataVersionService.getVersion());
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "contactSuccess", required = false) Boolean contactSuccess,
                       Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        return renderHome(contactSuccess, model);
    }

    private String renderHome(Boolean contactSuccess, Model model) {
        analyticsService.recordPortfolioView();

        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(new AboutInfo());
        List<EducationEntry> education = educationEntryRepository.findAllByOrderBySortOrderAsc();
        List<Skill> skills = skillRepository.findByVisibleTrueOrderByCategoryAscSortOrderAsc();
        List<String> skillCategories = skillRepository.findDistinctVisibleCategories();
        List<Experience> experiences = experienceRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<Project> featuredProjects = projectRepository.findByVisibleTrueAndFeaturedTrueOrderBySortOrderAsc();
        List<Project> projects = projectRepository.findVisibleNonFeaturedOrderBySortOrderAsc();
        List<Certificate> certificates = certificateRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<ServiceItem> services = serviceItemRepository.findByVisibleTrueOrderBySortOrderAsc();
        List<Testimonial> testimonials = testimonialRepository.findByPublishedTrue();
        List<BuildingProject> buildingProjects = buildingProjectRepository.findAllByOrderBySortOrderAsc();
        List<LearningProject> learningProjects = learningProjectRepository.findAllByOrderBySortOrderAsc();

        model.addAttribute("about", about);
        model.addAttribute("education", education);
        model.addAttribute("skills", skills);
        model.addAttribute("skillCategories", skillCategories);
        model.addAttribute("experiences", experiences);
        model.addAttribute("featuredProjects", featuredProjects);
        model.addAttribute("projects", projects);
        model.addAttribute("certificates", certificates);
        model.addAttribute("services", services);
        model.addAttribute("testimonials", testimonials);
        model.addAttribute("buildingProjects", buildingProjects);
        model.addAttribute("learningProjects", learningProjects);
        model.addAttribute("github", githubStatsService.fetchStats(about.getGithubUsername()));
        model.addAttribute("leetcodeRepo", leetCodeRepositoryStatsService.fetchStats(about.getGithubUsername()));

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
            return renderHome(null, model);
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
        String relativePath = resume.getFileUrl().replaceFirst("^/uploads/", "");
        Path filePath = Path.of(uploadDir, relativePath);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resume file not found on server");
            return;
        }

        resume.setDownloadCount(resume.getDownloadCount() + 1);
        resumeRepository.save(resume);
        analyticsService.recordResumeDownload();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Prince_Gupt_Resume.pdf\"");
        Files.copy(filePath, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
