package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Testimonial;
import com.example.portfolio.repository.TestimonialRepository;
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
@RequestMapping("/admin/testimonials")
public class AdminTestimonialController {

    private final TestimonialRepository testimonialRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    public AdminTestimonialController(TestimonialRepository testimonialRepository, FileStorageService fileStorageService, DataVersionService dataVersionService) {
        this.testimonialRepository = testimonialRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("testimonials", testimonialRepository.findAll());
        return "admin/testimonials/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("testimonial", new Testimonial());
        return "admin/testimonials/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("testimonial", testimonialRepository.findById(id).orElseThrow());
        return "admin/testimonials/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Testimonial testimonial,
                        @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (photoFile != null && !photoFile.isEmpty()) {
            testimonial.setPhotoUrl(fileStorageService.store(photoFile, "testimonials"));
        } else if (testimonial.getId() != null) {
            testimonialRepository.findById(testimonial.getId())
                    .ifPresent(existing -> testimonial.setPhotoUrl(existing.getPhotoUrl()));
        }
        if (testimonial.getPublished() == null) testimonial.setPublished(false);
        testimonialRepository.save(testimonial);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Testimonial from \"" + testimonial.getName() + "\" saved successfully.");
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/{id}/toggle-published")
    public String togglePublished(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        testimonialRepository.findById(id).ifPresent(t -> {
            t.setPublished(!Boolean.TRUE.equals(t.getPublished()));
            testimonialRepository.save(t);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Testimonial from \"" + t.getName() + "\" status set to " + (t.getPublished() ? "Published." : "Hidden."));
        });
        dataVersionService.bump();
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        testimonialRepository.deleteById(id);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Testimonial deleted successfully.");
        return "redirect:/admin/testimonials";
    }
}
