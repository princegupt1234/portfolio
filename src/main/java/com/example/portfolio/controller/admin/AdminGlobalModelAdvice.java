package com.example.portfolio.controller.admin;

import com.example.portfolio.repository.ContactMessageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.example.portfolio.controller.admin")
public class AdminGlobalModelAdvice {

    private final ContactMessageRepository contactMessageRepository;

    public AdminGlobalModelAdvice(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @ModelAttribute("unreadMessagesCount")
    public long unreadMessagesCount() {
        try {
            return contactMessageRepository.countByIsReadFalse();
        } catch (Exception e) {
            return 0;
        }
    }

    @ModelAttribute("currentAdminUsername")
    public String currentAdminUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Admin";
    }
}
