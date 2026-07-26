package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.ServiceItem;
import com.example.portfolio.repository.ServiceItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceItemController {

    private final ServiceItemRepository serviceItemRepository;

    public AdminServiceItemController(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
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
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        serviceItemRepository.deleteById(id);
        return "redirect:/admin/services";
    }
}
