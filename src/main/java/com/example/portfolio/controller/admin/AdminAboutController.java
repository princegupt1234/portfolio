package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.entity.EducationEntry;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.EducationEntryRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/about")
public class AdminAboutController {

    private final AboutInfoRepository aboutInfoRepository;
    private final EducationEntryRepository educationEntryRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    public AdminAboutController(AboutInfoRepository aboutInfoRepository,
                                 EducationEntryRepository educationEntryRepository,
                                 FileStorageService fileStorageService,
                                 DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.educationEntryRepository = educationEntryRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("education", educationEntryRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("newEducation", new EducationEntry());
        return "admin/about/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo about,
                        @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {
        if (photoFile != null && !photoFile.isEmpty()) {
            about.setProfileImage(fileStorageService.store(photoFile, "profile"));
        } else if (about.getId() != null) {
            aboutInfoRepository.findById(about.getId())
                    .ifPresent(existing -> about.setProfileImage(existing.getProfileImage()));
        }
        aboutInfoRepository.save(about);
        dataVersionService.bump();
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
}
