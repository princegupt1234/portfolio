package com.example.portfolio.controller.admin;

import com.example.portfolio.entity.ContactMessage;
import com.example.portfolio.repository.ContactMessageRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {

    private final ContactMessageRepository contactMessageRepository;

    public AdminMessageController(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
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
    public String reply(@PathVariable("id") Long id, @RequestParam("replyText") String replyText) {
        ContactMessage msg = contactMessageRepository.findById(id).orElseThrow();
        msg.setReplyText(replyText);
        contactMessageRepository.save(msg);
        return "redirect:/admin/messages/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        contactMessageRepository.deleteById(id);
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
