package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.Admin;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        Admin admin = null;
        if (auth != null && auth.getName() != null) {
            admin = adminRepository.findByUsername(auth.getName()).orElse(null);
        }
        if (admin == null) {
            admin = adminRepository.findAll().stream().findFirst().orElse(null);
        }
        if (admin == null) {
            return "redirect:/admin/login?logout=true";
        }
        model.addAttribute("admin", admin);
        return "admin/profile/edit";
    }

    @PostMapping("/save")
    public String save(@RequestParam("username") String username, @RequestParam("email") String email,
                        @RequestParam(value = "newPassword", required = false) String newPassword,
                        @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                        Authentication auth,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Admin admin = null;
        if (auth != null && auth.getName() != null) {
            admin = adminRepository.findByUsername(auth.getName()).orElse(null);
        }
        if (admin == null) {
            admin = adminRepository.findAll().stream().findFirst().orElse(null);
        }
        if (admin == null) {
            return "redirect:/admin/login?logout=true";
        }

        String oldUsername = admin.getUsername();
        admin.setUsername(username.trim());
        admin.setEmail(email.trim());
        if (newPassword != null && !newPassword.isBlank()) {
            admin.setPassword(passwordEncoder.encode(newPassword.trim()));
        }
        if (photoFile != null && !photoFile.isEmpty()) {
            admin.setProfileImage(fileStorageService.store(photoFile, "admin"));
        }
        adminRepository.save(admin);

        // If username changed, refresh Spring Security Authentication session so user stays logged in
        if (auth != null && !admin.getUsername().equals(oldUsername)) {
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            admin.getUsername(),
                            auth.getCredentials(),
                            auth.getAuthorities()
                    );
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/admin/profile?saved=true";
    }
}
