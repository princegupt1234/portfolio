package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.BuildingProject;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.entity.LearningProject;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.BuildingProjectRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.repository.LearningProjectRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Controller
@RequestMapping("/admin/about")
public class AdminAboutController {

    private final AboutInfoRepository aboutInfoRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final BuildingProjectRepository buildingProjectRepository;
    private final LearningProjectRepository learningProjectRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    @PersistenceContext
    private EntityManager em;

    public AdminAboutController(AboutInfoRepository aboutInfoRepository,
                                 EducationEntryRepository educationEntryRepository,
                                 BuildingProjectRepository buildingProjectRepository,
                                 LearningProjectRepository learningProjectRepository,
                                 FileStorageService fileStorageService,
                                 DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.buildingProjectRepository = buildingProjectRepository;
        this.learningProjectRepository = learningProjectRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping("/migrate")
    @ResponseBody
    @Transactional
    public String migrate() {
        try {
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS availability_text VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS availability_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS work_preferences_section_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS work_preference_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS preferred_locations_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS languages_spoken_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS footer_tagline VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS footer_sub TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS quick_stats TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS quick_stats_visible BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS work_preference TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS preferred_locations TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS languages_spoken TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS calendly_url TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS terminal_enabled BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS og_tags_enabled BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS og_title TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS og_description TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS og_image_url TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS contact_spam_protection_enabled BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS contact_honeypot_enabled BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS contact_rate_limit_seconds INT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS pwa_enabled BIT(1) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS pwa_app_name VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS pwa_short_name VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS pwa_theme_color VARCHAR(50) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE about_info ADD COLUMN IF NOT EXISTS pwa_background_color VARCHAR(50) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE projects ADD COLUMN IF NOT EXISTS engineering_highlight TEXT NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE projects ADD COLUMN IF NOT EXISTS demo_video_url VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery("ALTER TABLE projects ADD COLUMN IF NOT EXISTS architecture_image_url VARCHAR(255) NULL").executeUpdate();
            em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS building_project (" +
                "  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "  title VARCHAR(255)," +
                "  summary TEXT," +
                "  description TEXT," +
                "  tech_stack TEXT," +
                "  project_url VARCHAR(255)," +
                "  status VARCHAR(255)," +
                "  progress INT," +
                "  sort_order INT" +
                ")"
            ).executeUpdate();
            return "Migration OK — all columns and tables created. You can now remove this endpoint.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeSub", "about");
        model.addAttribute("education", educationEntryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("buildingProjects", buildingProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("editingBuildingProject", new BuildingProject());
        model.addAttribute("learningProjects", learningProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newLearningProject", new LearningProject());
        return "admin/about/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo form,
                        @RequestParam(value = "workPreferencesSectionVisible", required = false) String workPreferencesSectionVisibleParam,
                        @RequestParam(value = "availabilityVisible", required = false) String availabilityVisibleParam,
                        @RequestParam(value = "workPreferenceVisible", required = false) String workPreferenceVisibleParam,
                        @RequestParam(value = "preferredLocationsVisible", required = false) String preferredLocationsVisibleParam,
                        @RequestParam(value = "languagesSpokenVisible", required = false) String languagesSpokenVisibleParam,
                        @RequestParam(value = "terminalEnabled", required = false) String terminalEnabledParam,
                        @RequestParam(value = "recruiterPitchEnabled", required = false) String recruiterPitchEnabledParam,
                        @RequestParam(value = "aiChatEnabled", required = false) String aiChatEnabledParam,
                        @RequestParam(value = "quickStatsVisible", required = false) String quickStatsVisibleParam,
                        @RequestParam(value = "ogTagsEnabled", required = false) String ogTagsEnabledParam,
                        @RequestParam(value = "contactSpamProtectionEnabled", required = false) String contactSpamProtectionEnabledParam,
                        @RequestParam(value = "contactHoneypotEnabled", required = false) String contactHoneypotEnabledParam,
                        @RequestParam(value = "pwaEnabled", required = false) String pwaEnabledParam,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        // Update ONLY About Section fields so Hero Section data remains completely untouched
        existing.setCareerObjective(form.getCareerObjective());
        existing.setLocation(form.getLocation());
        existing.setPhone(form.getPhone());
        existing.setWorkPreferencesSectionVisible("true".equals(workPreferencesSectionVisibleParam));
        existing.setAvailabilityText(form.getAvailabilityText());
        existing.setAvailabilityVisible("true".equals(availabilityVisibleParam));
        existing.setWorkPreference(form.getWorkPreference());
        existing.setWorkPreferenceVisible("true".equals(workPreferenceVisibleParam));
        existing.setPreferredLocations(form.getPreferredLocations());
        existing.setPreferredLocationsVisible("true".equals(preferredLocationsVisibleParam));
        existing.setLanguagesSpoken(form.getLanguagesSpoken());
        existing.setLanguagesSpokenVisible("true".equals(languagesSpokenVisibleParam));
        existing.setCalendlyUrl(form.getCalendlyUrl());
        existing.setTerminalEnabled("true".equals(terminalEnabledParam));
        existing.setFooterTagline(form.getFooterTagline());
        existing.setFooterSub(form.getFooterSub());

        // Quick Credibility Stats
        if (form.getQuickStats() != null) {
            existing.setQuickStats(form.getQuickStats());
        }
        if (quickStatsVisibleParam != null) {
            existing.setQuickStatsVisible("true".equals(quickStatsVisibleParam));
        } else if (form.getQuickStats() != null) {
            existing.setQuickStatsVisible(false);
        }

        // Recruiter Modal
        existing.setRecruiterPitchEnabled("true".equals(recruiterPitchEnabledParam));
        existing.setRecruiterTargetRole(form.getRecruiterTargetRole());
        existing.setRecruiterMetrics(form.getRecruiterMetrics());
        existing.setRecruiterHighlights(form.getRecruiterHighlights());
        existing.setRecruiterPitchCopyText(form.getRecruiterPitchCopyText());

        // AI Chatbot Widget
        existing.setAiChatEnabled("true".equals(aiChatEnabledParam));
        existing.setAiChatWelcomeMessage(form.getAiChatWelcomeMessage());
        existing.setAiChatPromptChips(form.getAiChatPromptChips());

        // Social Sharing & Open Graph SEO
        existing.setOgTagsEnabled("true".equals(ogTagsEnabledParam));
        existing.setOgTitle(form.getOgTitle());
        existing.setOgDescription(form.getOgDescription());
        existing.setOgImageUrl(form.getOgImageUrl());

        // Contact Spam Protection & Rate Limiting
        existing.setContactSpamProtectionEnabled("true".equals(contactSpamProtectionEnabledParam));
        existing.setContactHoneypotEnabled("true".equals(contactHoneypotEnabledParam));
        if (form.getContactRateLimitSeconds() != null) {
            existing.setContactRateLimitSeconds(form.getContactRateLimitSeconds());
        }

        // Mobile Web App & PWA Settings
        existing.setPwaEnabled("true".equals(pwaEnabledParam));
        existing.setPwaAppName(form.getPwaAppName());
        existing.setPwaShortName(form.getPwaShortName());
        existing.setPwaThemeColor(form.getPwaThemeColor());
        existing.setPwaBackgroundColor(form.getPwaBackgroundColor());

        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "About & Interactive features updated successfully.");
        return "redirect:/admin/about";
    }

    @PostMapping("/education/save")
    public String saveEducation(@ModelAttribute EducationEntry educationEntry) {
        educationEntryRepository.save(educationEntry);
        dataVersionService.bump();
        return "redirect:/admin/about";
    }

    @PostMapping("/education/{id}/delete")
    public String deleteEducation(@PathVariable("id") Long id) {
        educationEntryRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about";
    }

    @GetMapping("/building/{id}/edit")
    public String editBuildingProjectForm(@PathVariable("id") Long id, Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeSub", "about");
        model.addAttribute("education", educationEntryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newEducation", new EducationEntry());
        model.addAttribute("buildingProjects", buildingProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("editingBuildingProject", buildingProjectRepository.findById(id).orElseThrow());
        model.addAttribute("learningProjects", learningProjectRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newLearningProject", new LearningProject());
        return "admin/about/edit";
    }

    @PostMapping("/building/save")
    public String saveBuildingProject(@ModelAttribute BuildingProject project) {
        buildingProjectRepository.save(project);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-building";
    }

    @PostMapping("/building/{id}/delete")
    public String deleteBuildingProject(@PathVariable("id") Long id) {
        buildingProjectRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-building";
    }

    // ── REST API for inline CRUD (used by JS fetch) ──────────────────────────

    @GetMapping("/api/building")
    @ResponseBody
    public List<BuildingProject> apiListBuilding() {
        return buildingProjectRepository.findAllByOrderBySortOrderAsc();
    }

    @PostMapping("/api/building")
    @ResponseBody
    public ResponseEntity<BuildingProject> apiCreateBuilding(@RequestBody BuildingProject project) {
        project.setId(null); // ensure insert not update
        if (!isValidBuildingProject(project))
            return ResponseEntity.badRequest().build();
        BuildingProject saved = buildingProjectRepository.save(project);
        dataVersionService.bump();
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/api/building/{id}")
    @ResponseBody
    public ResponseEntity<BuildingProject> apiUpdateBuilding(@PathVariable Long id,
                                                              @RequestBody BuildingProject project) {
        if (!buildingProjectRepository.existsById(id))
            return ResponseEntity.notFound().build();
        if (!isValidBuildingProject(project))
            return ResponseEntity.<BuildingProject>badRequest().build();
        BuildingProject existing = buildingProjectRepository.findById(id).get();
        existing.setTitle(project.getTitle());
        existing.setStatus(project.getStatus());
        existing.setProgress(project.getProgress());
        existing.setSortOrder(project.getSortOrder());
        existing.setProjectUrl(project.getProjectUrl());
        existing.setDescription(project.getDescription());
        existing.setSummary(project.getSummary());
        existing.setTechStack(project.getTechStack());
        dataVersionService.bump();
        return ResponseEntity.ok(buildingProjectRepository.save(existing));
    }

    private boolean isValidBuildingProject(BuildingProject project) {
        if (project.getTitle() == null || project.getTitle().isBlank()) return false;
        if (project.getStatus() == null || project.getStatus().isBlank()) return false;
        if (project.getSortOrder() != null && project.getSortOrder() < 0) return false;
        if (project.getProjectUrl() == null || project.getProjectUrl().isBlank()) return true;
        try {
            URI uri = new URI(project.getProjectUrl());
            return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    @DeleteMapping("/api/building/{id}")
    @ResponseBody
    public ResponseEntity<Void> apiDeleteBuilding(@PathVariable Long id) {
        if (!buildingProjectRepository.existsById(id))
            return ResponseEntity.notFound().build();
        buildingProjectRepository.deleteById(id);
        dataVersionService.bump();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/learning/save")
    public String saveLearningProject(@ModelAttribute LearningProject project) {
        learningProjectRepository.save(project);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-learning";
    }

    @PostMapping("/learning/{id}/delete")
    public String deleteLearningProject(@PathVariable("id") Long id) {
        learningProjectRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/about#sec-learning";
    }

    @PostMapping("/learning/{id}/move")
    public String moveLearningProject(@PathVariable("id") Long id, @RequestParam("dir") int dir) {
        var all = learningProjectRepository.findAllByOrderBySortOrderAsc();
        int idx = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) { idx = i; break; }
        }
        int target = idx + dir;
        if (idx >= 0 && target >= 0 && target < all.size()) {
            int tmp = all.get(idx).getSortOrder() != null ? all.get(idx).getSortOrder() : idx;
            int tgt = all.get(target).getSortOrder() != null ? all.get(target).getSortOrder() : target;
            all.get(idx).setSortOrder(tgt);
            all.get(target).setSortOrder(tmp);
            learningProjectRepository.saveAll(all);
            dataVersionService.bump();
        }
        return "redirect:/admin/about#sec-learning";
    }
}
