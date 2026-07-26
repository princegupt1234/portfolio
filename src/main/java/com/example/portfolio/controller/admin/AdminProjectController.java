package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Project;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    public AdminProjectController(ProjectRepository projectRepository, FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectRepository.findAllByOrderBySortOrderAsc());
        return "admin/projects/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        return "admin/projects/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("project", projectRepository.findById(id).orElseThrow());
        return "admin/projects/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Project project,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            project.setImageUrl(fileStorageService.store(imageFile, "projects"));
        } else if (project.getId() != null) {
            // keep existing image if no new file uploaded
            projectRepository.findById(project.getId()).ifPresent(existing -> {
                if (project.getImageUrl() == null || project.getImageUrl().isBlank()) {
                    project.setImageUrl(existing.getImageUrl());
                }
            });
        }
        projectRepository.save(project);
        return "redirect:/admin/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        projectRepository.deleteById(id);
        return "redirect:/admin/projects";
    }
}
