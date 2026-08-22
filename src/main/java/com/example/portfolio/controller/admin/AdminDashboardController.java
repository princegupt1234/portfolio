package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.CertificateRepository;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ResumeRepository;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.service.AnalyticsService;
import com.example.portfolio.service.GithubStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final CertificateRepository certificateRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ResumeRepository resumeRepository;
    private final AboutInfoRepository aboutInfoRepository;
    private final AnalyticsService analyticsService;
    private final GithubStatsService githubStatsService;

    public AdminDashboardController(ProjectRepository projectRepository, SkillRepository skillRepository,
                                     CertificateRepository certificateRepository,
                                     ContactMessageRepository contactMessageRepository,
                                     ResumeRepository resumeRepository, AboutInfoRepository aboutInfoRepository,
                                     AnalyticsService analyticsService, GithubStatsService githubStatsService) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.certificateRepository = certificateRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.resumeRepository = resumeRepository;
        this.aboutInfoRepository = aboutInfoRepository;
        this.analyticsService = analyticsService;
        this.githubStatsService = githubStatsService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        var about = aboutInfoRepository.findAll().stream().findFirst().orElse(null);

        model.addAttribute("projectCount", projectRepository.count());
        model.addAttribute("skillCount", skillRepository.count());
        model.addAttribute("certificateCount", certificateRepository.count());
        model.addAttribute("unreadMessages", contactMessageRepository.countByIsReadFalse());
        model.addAttribute("totalMessages", contactMessageRepository.count());
        model.addAttribute("resumeVersions", resumeRepository.count());

        List<SiteStat> last30 = analyticsService.last30Days();
        List<SiteStat> chronological = new java.util.ArrayList<>(last30);
        java.util.Collections.reverse(chronological);
        model.addAttribute("stats", chronological);

        long totalViews = last30.stream().mapToLong(SiteStat::getPortfolioViews).sum();
        long totalDownloads = last30.stream().mapToLong(SiteStat::getResumeDownloads).sum();
        long totalClicks = last30.stream().mapToLong(SiteStat::getProjectClicks).sum();
        model.addAttribute("totalViews30d", totalViews);
        model.addAttribute("totalDownloads30d", totalDownloads);
        model.addAttribute("totalClicks30d", totalClicks);

        long todayViews = last30.stream()
                .filter(s -> s.getStatDate().isEqual(java.time.LocalDate.now()))
                .mapToLong(SiteStat::getPortfolioViews).sum();
        model.addAttribute("todayViews", todayViews);

        // profile completion heuristic
        int completed = 0, total = 6;
        if (about != null) {
            if (about.getBio() != null && !about.getBio().isBlank()) completed++;
            if (about.getProfileImage() != null) completed++;
            if (about.getResumeUrl() != null) completed++;
            if (about.getGithubUrl() != null) completed++;
            if (about.getLinkedinUrl() != null) completed++;
        }
        if (projectRepository.count() > 0) completed++;
        model.addAttribute("profileCompletion", (int) (100.0 * completed / total));

        model.addAttribute("github", about == null ? null : githubStatsService.fetchStats(about.getGithubUsername()));

        return "admin/dashboard";
    }
}
