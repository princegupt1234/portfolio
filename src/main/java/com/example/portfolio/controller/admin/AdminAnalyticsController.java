package com.example.portfolio.controller.admin;

import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;
    private final ProjectRepository projectRepository;

    public AdminAnalyticsController(AnalyticsService analyticsService, ProjectRepository projectRepository) {
        this.analyticsService = analyticsService;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public String analytics(Model model) {
        model.addAttribute("stats", analyticsService.last30Days());
        model.addAttribute("projects", projectRepository.findAllByOrderBySortOrderAsc());
        return "admin/analytics/index";
    }
}
