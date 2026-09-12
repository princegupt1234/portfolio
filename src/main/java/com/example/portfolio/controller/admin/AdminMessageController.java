package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.repository.ContactMessageRepository;
import com.example.portfolio.service.MailService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {

    private final ContactMessageRepository contactMessageRepository;
    private final MailService mailService;

    public AdminMessageController(ContactMessageRepository contactMessageRepository, MailService mailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.mailService = mailService;
    }

    @GetMapping
    public String inbox(@RequestParam(value = "q", required = false) String q, Model model) {
        List<ContactMessage> messages = contactMessageRepository.findAllByOrderByCreatedAtDesc();
        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase();
            messages = messages.stream()
                    .filter(m -> (m.getName() != null && m.getName().toLowerCase().contains(needle))
                            || (m.getEmail() != null && m.getEmail().toLowerCase().contains(needle))
                            || (m.getSubject() != null && m.getSubject().toLowerCase().contains(needle))
                            || (m.getMessage() != null && m.getMessage().toLowerCase().contains(needle)))
                    .toList();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("q", q);
        return "admin/messages/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        ContactMessage msg = contactMessageRepository.findById(id).orElseThrow();
        if (!Boolean.TRUE.equals(msg.getIsRead())) {
            msg.setIsRead(true);
            contactMessageRepository.save(msg);
        }
        model.addAttribute("message", msg);
        return "admin/messages/view";
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable("id") Long id,
                        @RequestParam("replyText") String replyText) {
        ContactMessage msg = contactMessageRepository.findById(id).orElseThrow();
        msg.setReplyText(replyText);
        String replySubject = "Re: " + (msg.getSubject() == null || msg.getSubject().isBlank() ? "Your message" : msg.getSubject());
        MailService.MailResult result = mailService.sendReplyWithResult(msg.getEmail(), msg.getName(), replySubject, replyText, msg.getMessage());
        boolean sent = (result == MailService.MailResult.SUCCESS);
        if (sent) msg.setRepliedAt(LocalDateTime.now());
        contactMessageRepository.save(msg);
        return "redirect:/admin/messages/" + id + "?sent=" + sent + "&reason=" + result.name().toLowerCase();
    }

    @PostMapping("/{id}/toggle-read")
    public String toggleRead(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        contactMessageRepository.findById(id).ifPresent(m -> {
            boolean newStatus = !Boolean.TRUE.equals(m.getIsRead());
            m.setIsRead(newStatus);
            contactMessageRepository.save(m);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Message from \"" + m.getName() + "\" marked as " + (newStatus ? "Read." : "Unread."));
        });
        return "redirect:/admin/messages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        contactMessageRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Contact message deleted successfully.");
        return "redirect:/admin/messages";
    }

    @GetMapping("/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportCsv() {
        StringBuilder sb = new StringBuilder("ID,Name,Email,Subject,Message,Read,CreatedAt\n");
        for (ContactMessage m : contactMessageRepository.findAllByOrderByCreatedAtDesc()) {
            sb.append(m.getId()).append(',')
              .append(csv(m.getName())).append(',')
              .append(csv(m.getEmail())).append(',')
              .append(csv(m.getSubject())).append(',')
              .append(csv(m.getMessage())).append(',')
              .append(m.getIsRead()).append(',')
              .append(m.getCreatedAt()).append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contact_messages.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    private String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"").replace("\n", " ") + "\"";
    }
}
