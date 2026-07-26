package com.example.portfolio.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

/**
 * Renders the admin login page. Spring Security handles the actual
 * authentication POST to /admin/login (see SecurityConfig).
 */
@Controller
public class AdminLoginController {

    @GetMapping("/admin/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "logout", required = false) String logout,
                             Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out");
        }
        return "admin/login";
    }
}
