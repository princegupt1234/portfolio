package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/hero")
public class AdminHeroController {

    private final AboutInfoRepository aboutInfoRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    public AdminHeroController(AboutInfoRepository aboutInfoRepository,
                               FileStorageService fileStorageService,
                               DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeSub", "hero");
        return "admin/hero/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo form,
                       @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                       @RequestParam(value = "quickStatsVisible", required = false) String quickStatsVisibleParam,
                       RedirectAttributes redirectAttributes) {
        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        // Update ONLY Hero Section fields so About Section data remains completely untouched
        existing.setFullName(form.getFullName());
        existing.setTitle(form.getTitle());
        existing.setHeroEyebrow(form.getHeroEyebrow());
        existing.setBio(form.getBio());
        existing.setHeroPhrases(form.getHeroPhrases());
        existing.setHeroTypingSpeed(form.getHeroTypingSpeed());
        existing.setHeroDeletingSpeed(form.getHeroDeletingSpeed());
        existing.setHeroPauseDuration(form.getHeroPauseDuration());
        existing.setHeroPrimaryCtaLabel(form.getHeroPrimaryCtaLabel());
        existing.setHeroPrimaryCtaLink(form.getHeroPrimaryCtaLink());
        existing.setHeroSecondaryCtaLabel(form.getHeroSecondaryCtaLabel());
        existing.setHeroSecondaryCtaLink(form.getHeroSecondaryCtaLink());
        existing.setHeroTechStack(form.getHeroTechStack());
        existing.setGithubUrl(form.getGithubUrl());
        existing.setLinkedinUrl(form.getLinkedinUrl());
        existing.setWhatsappUrl(form.getWhatsappUrl());
        existing.setEmail(form.getEmail());
        existing.setQuickStats(form.getQuickStats());
        existing.setQuickStatsVisible("true".equals(quickStatsVisibleParam));

        if (photoFile != null && !photoFile.isEmpty()) {
            existing.setProfileImage(fileStorageService.store(photoFile, "profile"));
        }

        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Hero Section updated successfully.");
        return "redirect:/admin/hero";
    }
}
