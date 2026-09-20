package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Experience;
import com.example.portfolio.repository.ExperienceRepository;
import com.example.portfolio.service.DataVersionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/experience")
public class AdminExperienceController {

    private final ExperienceRepository experienceRepository;
    private final DataVersionService dataVersionService;

    public AdminExperienceController(ExperienceRepository experienceRepository, DataVersionService dataVersionService) {
        this.experienceRepository = experienceRepository;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("experiences", experienceRepository.findAllByOrderBySortOrderAsc());
        return "admin/experience/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("experience", new Experience());
        return "admin/experience/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        return experienceRepository.findById(id)
                .map(experience -> {
                    model.addAttribute("experience", experience);
                    return "admin/experience/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Experience not found.");
                    return "redirect:/admin/experience";
                });
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Experience experience, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        experienceRepository.save(experience);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Experience \"" + experience.getRole() + "\" saved successfully.");
        return "redirect:/admin/experience";
    }

    @PostMapping("/{id}/toggle-visible")
    public String toggleVisible(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        return experienceRepository.findById(id)
                .map(e -> {
                    e.setVisible(!Boolean.TRUE.equals(e.getVisible()));
                    experienceRepository.save(e);
                    dataVersionService.bump();
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Experience \"" + e.getRole() + "\" visibility set to " + (e.getVisible() ? "Visible." : "Hidden."));
                    return "redirect:/admin/experience";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Experience not found.");
                    return "redirect:/admin/experience";
                });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!experienceRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Experience not found.");
            return "redirect:/admin/experience";
        }
        experienceRepository.deleteById(id);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Experience deleted successfully.");
        return "redirect:/admin/experience";
    }
}
