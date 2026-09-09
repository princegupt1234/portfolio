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
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("service", serviceItemRepository.findById(id).orElseThrow());
        return "admin/services/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("service") ServiceItem service) {
        serviceItemRepository.save(service);
        dataVersionService.bump();
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-visible")
    public String toggleVisible(@PathVariable("id") Long id) {
        serviceItemRepository.findById(id).ifPresent(s -> {
            s.setVisible(!Boolean.TRUE.equals(s.getVisible()));
            serviceItemRepository.save(s);
        });
        dataVersionService.bump();
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        serviceItemRepository.deleteById(id);
        dataVersionService.bump();
        return "redirect:/admin/services";
    }
}
