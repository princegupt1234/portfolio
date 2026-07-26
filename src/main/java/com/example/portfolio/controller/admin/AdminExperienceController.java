package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Experience;
import com.example.portfolio.repository.ExperienceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/experience")
public class AdminExperienceController {

    private final ExperienceRepository experienceRepository;

    public AdminExperienceController(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
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
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("experience", experienceRepository.findById(id).orElseThrow());
        return "admin/experience/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Experience experience) {
        experienceRepository.save(experience);
        return "redirect:/admin/experience";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        experienceRepository.deleteById(id);
        return "redirect:/admin/experience";
    }
}
