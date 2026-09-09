package com.example.portfolio.controller.admin;


import com.example.portfolio.entity.Resume;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.repository.ResumeRepository;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/resume")
public class AdminResumeController {

    private final ResumeRepository resumeRepository;
    private final AboutInfoRepository aboutInfoRepository;
    private final FileStorageService fileStorageService;

    public AdminResumeController(ResumeRepository resumeRepository, AboutInfoRepository aboutInfoRepository,
                                  FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.aboutInfoRepository = aboutInfoRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("resumes", resumeRepository.findAllByOrderByUploadedAtDesc());
        return "admin/resume/list";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("resumeFile") MultipartFile resumeFile) {
        if (resumeFile != null && !resumeFile.isEmpty()) {
            String url = fileStorageService.store(resumeFile, "resume");

            // deactivate previous versions
            resumeRepository.findAll().forEach(r -> {
                r.setActive(false);
                resumeRepository.save(r);
            });

            Resume resume = new Resume();
            resume.setFileUrl(url);
            long version = resumeRepository.count() + 1;
            resume.setVersionLabel("v" + version);
            resume.setActive(true);
            resumeRepository.save(resume);

            // keep AboutInfo.resumeUrl in sync so the hero "Resume" button works
            aboutInfoRepository.findAll().stream().findFirst().ifPresent(about -> {
                about.setResumeUrl(url);
                aboutInfoRepository.save(about);
            });
        }
        return "redirect:/admin/resume";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable("id") Long id) {
        resumeRepository.findAll().forEach(r -> {
            r.setActive(false);
            resumeRepository.save(r);
        });
        resumeRepository.findById(id).ifPresent(r -> {
            r.setActive(true);
            resumeRepository.save(r);
            aboutInfoRepository.findAll().stream().findFirst().ifPresent(about -> {
                about.setResumeUrl(r.getFileUrl());
                aboutInfoRepository.save(about);
            });
        });
        return "redirect:/admin/resume";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        resumeRepository.deleteById(id);
        return "redirect:/admin/resume";
    }
}
