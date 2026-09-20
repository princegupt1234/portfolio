package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/sections")
public class AdminSectionController {

    private final AboutInfoRepository aboutInfoRepository;
    private final DataVersionService dataVersionService;

    public AdminSectionController(AboutInfoRepository aboutInfoRepository,
                                  DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String index(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeNav", "sections");
        return "admin/sections/index";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "sectionHeroVisible", required = false) String heroParam,
                       @RequestParam(value = "sectionQuickStatsVisible", required = false) String quickStatsParam,
                       @RequestParam(value = "sectionAboutVisible", required = false) String aboutParam,
                       @RequestParam(value = "sectionSkillsVisible", required = false) String skillsParam,
                       @RequestParam(value = "sectionExperienceVisible", required = false) String experienceParam,
                       @RequestParam(value = "sectionProjectsVisible", required = false) String projectsParam,
                       @RequestParam(value = "sectionCodingVisible", required = false) String codingParam,
                       @RequestParam(value = "sectionCertificatesVisible", required = false) String certificatesParam,
                       @RequestParam(value = "sectionCurrentlyVisible", required = false) String currentlyParam,
                       @RequestParam(value = "sectionServicesVisible", required = false) String servicesParam,
                       @RequestParam(value = "sectionTestimonialsVisible", required = false) String testimonialsParam,
                       @RequestParam(value = "sectionContactVisible", required = false) String contactParam,
                       RedirectAttributes redirectAttributes) {

        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        existing.setSectionHeroVisible("true".equalsIgnoreCase(heroParam));
        existing.setSectionQuickStatsVisible("true".equalsIgnoreCase(quickStatsParam));
        existing.setSectionAboutVisible("true".equalsIgnoreCase(aboutParam));
        existing.setSectionSkillsVisible("true".equalsIgnoreCase(skillsParam));
        existing.setSectionExperienceVisible("true".equalsIgnoreCase(experienceParam));
        existing.setSectionProjectsVisible("true".equalsIgnoreCase(projectsParam));
        existing.setSectionCodingVisible("true".equalsIgnoreCase(codingParam));
        existing.setSectionCertificatesVisible("true".equalsIgnoreCase(certificatesParam));
        existing.setSectionCurrentlyVisible("true".equalsIgnoreCase(currentlyParam));
        existing.setSectionServicesVisible("true".equalsIgnoreCase(servicesParam));
        existing.setSectionTestimonialsVisible("true".equalsIgnoreCase(testimonialsParam));
        existing.setSectionContactVisible("true".equalsIgnoreCase(contactParam));

        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Section visibility settings updated successfully.");
        return "redirect:/admin/sections";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleSection(@RequestParam("section") String section,
                                                             @RequestParam("visible") Boolean visible) {
        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        switch (section.toLowerCase().trim()) {
            case "hero" -> existing.setSectionHeroVisible(visible);
            case "quickstats", "quick-stats" -> existing.setSectionQuickStatsVisible(visible);
            case "about" -> existing.setSectionAboutVisible(visible);
            case "skills" -> existing.setSectionSkillsVisible(visible);
            case "experience" -> existing.setSectionExperienceVisible(visible);
            case "projects" -> existing.setSectionProjectsVisible(visible);
            case "coding" -> existing.setSectionCodingVisible(visible);
            case "certificates" -> existing.setSectionCertificatesVisible(visible);
            case "currently" -> existing.setSectionCurrentlyVisible(visible);
            case "services" -> existing.setSectionServicesVisible(visible);
            case "testimonials" -> existing.setSectionTestimonialsVisible(visible);
            case "contact" -> existing.setSectionContactVisible(visible);
            default -> {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Unknown section: " + section
                ));
            }
        }

        aboutInfoRepository.save(existing);
        dataVersionService.bump();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "section", section,
                "visible", visible,
                "message", (visible ? "Enabled" : "Disabled") + " section on frontend."
        ));
    }
}
