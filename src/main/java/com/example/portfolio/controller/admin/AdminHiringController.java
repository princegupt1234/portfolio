package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/hiring")
public class AdminHiringController {

    private final AboutInfoRepository aboutInfoRepository;
    private final DataVersionService dataVersionService;

    public AdminHiringController(AboutInfoRepository aboutInfoRepository, DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeSub", "hiring");
        return "admin/hiring/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo form,
                       @RequestParam(value = "availabilityVisible", required = false) String availabilityVisibleParam,
                       @RequestParam(value = "workPreferencesSectionVisible", required = false) String workPrefSecVisibleParam,
                       @RequestParam(value = "workPreferenceVisible", required = false) String workPrefVisibleParam,
                       @RequestParam(value = "preferredLocationsVisible", required = false) String prefLocVisibleParam,
                       @RequestParam(value = "languagesSpokenVisible", required = false) String langVisibleParam,
                       @RequestParam(value = "recruiterPitchEnabled", required = false) String recruiterPitchEnabledParam,
                       RedirectAttributes redirectAttributes) {

        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        // 1. Hiring Availability Controls
        existing.setAvailabilityVisible("true".equalsIgnoreCase(availabilityVisibleParam));
        existing.setAvailabilityText(form.getAvailabilityText());
        existing.setHiringRoles(form.getHiringRoles());
        existing.setHiringNoticePeriod(form.getHiringNoticePeriod());
        existing.setHiringLocationDetails(form.getHiringLocationDetails());
        existing.setHiringContactEmail(form.getHiringContactEmail());
        existing.setHiringCustomNote(form.getHiringCustomNote());

        // 2. Work Preferences & Badges
        existing.setWorkPreferencesSectionVisible("true".equalsIgnoreCase(workPrefSecVisibleParam));
        existing.setWorkPreference(form.getWorkPreference());
        existing.setWorkPreferenceVisible("true".equalsIgnoreCase(workPrefVisibleParam));
        existing.setPreferredLocations(form.getPreferredLocations());
        existing.setPreferredLocationsVisible("true".equalsIgnoreCase(prefLocVisibleParam));
        existing.setLanguagesSpoken(form.getLanguagesSpoken());
        existing.setLanguagesSpokenVisible("true".equalsIgnoreCase(langVisibleParam));

        // 3. 30-Second Recruiter Brief Modal
        existing.setRecruiterPitchEnabled("true".equalsIgnoreCase(recruiterPitchEnabledParam));
        existing.setRecruiterTargetRole(form.getRecruiterTargetRole());
        existing.setRecruiterMetrics(form.getRecruiterMetrics());
        existing.setRecruiterHighlights(form.getRecruiterHighlights());
        existing.setRecruiterPitchCopyText(form.getRecruiterPitchCopyText());

        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Hiring & Career Availability settings saved successfully.");
        return "redirect:/admin/hiring";
    }
}
