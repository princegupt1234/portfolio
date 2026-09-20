package com.example.portfolio.controller.admin;

import com.example.portfolio.service.BackupService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/backup")
public class AdminBackupController {

    private final BackupService backupService;

    public AdminBackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("activeNav", "backup");
        model.addAttribute("counts", backupService.getEntityCounts());
        return "admin/backup/index";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBackup() {
        byte[] data = backupService.exportBackupBytes();
        String filename = "portfolio-backup-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".json";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/restore")
    public String restoreBackup(@RequestParam("backupFile") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid JSON backup file to restore.");
            return "redirect:/admin/backup";
        }

        try {
            int restoredCount = backupService.restoreBackup(file.getInputStream());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Database restored successfully! Total " + restoredCount + " records restored.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Database restore failed: " + e.getMessage());
        }

        return "redirect:/admin/backup";
    }
}
