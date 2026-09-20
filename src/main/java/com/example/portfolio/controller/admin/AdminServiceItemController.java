package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.repository.ServiceItemRepository;
import com.example.portfolio.service.DataVersionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceItemController {

    private final ServiceItemRepository serviceItemRepository;
    private final DataVersionService dataVersionService;

    public AdminServiceItemController(ServiceItemRepository serviceItemRepository, DataVersionService dataVersionService) {
        this.serviceItemRepository = serviceItemRepository;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", serviceItemRepository.findAllByOrderBySortOrderAsc());
        return "admin/services/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("service", new ServiceItem());
        return "admin/services/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        return serviceItemRepository.findById(id)
                .map(service -> {
                    model.addAttribute("service", service);
                    return "admin/services/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Service not found.");
                    return "redirect:/admin/services";
                });
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("service") ServiceItem service, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        serviceItemRepository.save(service);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Service \"" + service.getTitle() + "\" saved successfully.");
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-visible")
    public String toggleVisible(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        return serviceItemRepository.findById(id)
                .map(s -> {
                    s.setVisible(!Boolean.TRUE.equals(s.getVisible()));
                    serviceItemRepository.save(s);
                    dataVersionService.bump();
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Service \"" + s.getTitle() + "\" visibility set to " + (s.getVisible() ? "Visible." : "Hidden."));
                    return "redirect:/admin/services";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Service not found.");
                    return "redirect:/admin/services";
                });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!serviceItemRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Service not found.");
            return "redirect:/admin/services";
        }
        serviceItemRepository.deleteById(id);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Service deleted successfully.");
        return "redirect:/admin/services";
    }
}
