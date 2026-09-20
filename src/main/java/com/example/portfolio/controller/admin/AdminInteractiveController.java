package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import com.example.portfolio.service.DataVersionService;
import com.example.portfolio.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/interactive")
public class AdminInteractiveController {

    private final AboutInfoRepository aboutInfoRepository;
    private final FileStorageService fileStorageService;
    private final DataVersionService dataVersionService;

    public AdminInteractiveController(AboutInfoRepository aboutInfoRepository,
                                      FileStorageService fileStorageService,
                                      DataVersionService dataVersionService) {
        this.aboutInfoRepository = aboutInfoRepository;
        this.fileStorageService = fileStorageService;
        this.dataVersionService = dataVersionService;
    }

    @GetMapping
    public String edit(Model model) {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);
        model.addAttribute("about", about);
        model.addAttribute("activeSub", "interactive");
        return "admin/interactive/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AboutInfo form,
                       @RequestParam(value = "aiChatEnabled", required = false) String aiChatEnabledParam,
                       @RequestParam(value = "terminalEnabled", required = false) String terminalEnabledParam,
                       @RequestParam(value = "ogTagsEnabled", required = false) String ogTagsEnabledParam,
                       @RequestParam(value = "ogImageFile", required = false) MultipartFile ogImageFile,
                       @RequestParam(value = "contactSpamProtectionEnabled", required = false) String spamProtectionParam,
                       @RequestParam(value = "contactHoneypotEnabled", required = false) String honeypotParam,
                       @RequestParam(value = "whatsappNotificationEnabled", required = false) String whatsappEnabledParam,
                       RedirectAttributes redirectAttributes) {

        AboutInfo existing = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        // 1. AI Chatbot
        existing.setAiChatEnabled("true".equalsIgnoreCase(aiChatEnabledParam));
        existing.setAiChatWelcomeMessage(form.getAiChatWelcomeMessage());
        existing.setAiChatPromptChips(form.getAiChatPromptChips());

        // 2. Terminal CLI & Custom Commands
        existing.setTerminalEnabled("true".equalsIgnoreCase(terminalEnabledParam));
        String rawCli = form.getCustomCliCommands();
        if (rawCli == null || rawCli.isBlank()) {
            existing.setCustomCliCommands("[]");
        } else {
            existing.setCustomCliCommands(rawCli.trim());
        }

        // 3. Social Sharing & SEO Open Graph
        existing.setOgTagsEnabled("true".equalsIgnoreCase(ogTagsEnabledParam));
        existing.setOgTitle(form.getOgTitle());
        existing.setOgDescription(form.getOgDescription());
        if (ogImageFile != null && !ogImageFile.isEmpty()) {
            existing.setOgImageUrl(fileStorageService.store(ogImageFile, "og"));
        } else if (form.getOgImageUrl() != null && !form.getOgImageUrl().isBlank()) {
            existing.setOgImageUrl(form.getOgImageUrl().trim());
        }

        // 4. Contact Spam Protection & Rate Limiting
        existing.setContactSpamProtectionEnabled("true".equalsIgnoreCase(spamProtectionParam));
        existing.setContactHoneypotEnabled("true".equalsIgnoreCase(honeypotParam));
        if (form.getContactRateLimitSeconds() != null && form.getContactRateLimitSeconds() >= 0) {
            existing.setContactRateLimitSeconds(form.getContactRateLimitSeconds());
        }

        // 5. Admin WhatsApp Notification Alert Settings
        existing.setWhatsappNotificationEnabled("true".equalsIgnoreCase(whatsappEnabledParam));
        existing.setWhatsappNotificationPhone(form.getWhatsappNotificationPhone());
        existing.setWhatsappNotificationApiKey(form.getWhatsappNotificationApiKey());
        existing.setWhatsappNotificationWebhookUrl(form.getWhatsappNotificationWebhookUrl());

        // 6. Outbound Email & Inquiry Notification Settings
        existing.setMailNotificationEmail(form.getMailNotificationEmail());
        existing.setResendApiKey(form.getResendApiKey());
        existing.setResendFrom(form.getResendFrom());
        existing.setBrevoApiKey(form.getBrevoApiKey());
        existing.setBrevoSenderEmail(form.getBrevoSenderEmail());
        existing.setBrevoSenderName(form.getBrevoSenderName());
        if (form.getMailSendingMethod() != null && !form.getMailSendingMethod().isBlank()) {
            existing.setMailSendingMethod(form.getMailSendingMethod().trim().toUpperCase());
        }

        aboutInfoRepository.save(existing);
        dataVersionService.bump();
        redirectAttributes.addFlashAttribute("successMessage", "Interactive tools & system settings saved successfully.");
        return "redirect:/admin/interactive";
    }
}
