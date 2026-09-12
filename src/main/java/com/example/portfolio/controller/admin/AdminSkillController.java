package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Skill;
import com.example.portfolio.repository.SkillRepository;
import com.example.portfolio.service.DataVersionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final SkillRepository skillRepository;
    private final DataVersionService dataVersionService;

    public AdminSkillController(SkillRepository skillRepository, DataVersionService dataVersionService) {
        this.skillRepository = skillRepository;
        this.dataVersionService = dataVersionService;
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
    public String save(@ModelAttribute Skill skill, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        skillRepository.save(skill);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Skill \"" + skill.getName() + "\" saved successfully.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/toggle-visible")
    public String toggleVisible(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        skillRepository.findById(id).ifPresent(s -> {
            s.setVisible(!Boolean.TRUE.equals(s.getVisible()));
            skillRepository.save(s);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Skill \"" + s.getName() + "\" visibility set to " + (s.getVisible() ? "Visible." : "Hidden."));
        });
        dataVersionService.bump();
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        skillRepository.deleteById(id);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Skill deleted successfully.");
        return "redirect:/admin/skills";
    }
}
