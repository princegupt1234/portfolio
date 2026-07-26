package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Admin;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/profile")
public class AdminProfileController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public AdminProfileController(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                                   FileStorageService fileStorageService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String edit(Model model, Authentication auth) {
        Admin admin = adminRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("admin", admin);
        return "admin/profile/edit";
    }

    @PostMapping("/save")
    public String save(@RequestParam("username") String username, @RequestParam("email") String email,
                        @RequestParam(value = "newPassword", required = false) String newPassword,
                        @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                        Authentication auth) {
        Admin admin = adminRepository.findByUsername(auth.getName()).orElseThrow();
        admin.setUsername(username);
        admin.setEmail(email);
        if (newPassword != null && !newPassword.isBlank()) {
            admin.setPassword(passwordEncoder.encode(newPassword));
        }
        if (photoFile != null && !photoFile.isEmpty()) {
            admin.setProfileImage(fileStorageService.store(photoFile, "admin"));
        }
        adminRepository.save(admin);
        return "redirect:/admin/profile?saved=true";
    }
}
