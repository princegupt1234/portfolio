package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Skill;
import com.example.portfolio.repository.SkillRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final SkillRepository skillRepository;

    public AdminSkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("skills", skillRepository.findAllByOrderByCategoryAscSortOrderAsc());
        return "admin/skills/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("skill", new Skill());
        return "admin/skills/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("skill", skillRepository.findById(id).orElseThrow());
        return "admin/skills/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Skill skill) {
        skillRepository.save(skill);
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        skillRepository.deleteById(id);
        return "redirect:/admin/skills";
    }
}
