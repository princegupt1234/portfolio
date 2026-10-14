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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/admin/backup")
public class AdminBackupController {

    private static final long MAX_BACKUP_SIZE_BYTES = 100L * 1024 * 1024;

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
        String filename = "portfolio-backup.json";

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
            BackupService.RestoreResult result;
            try (InputStream inputStream = file.getInputStream()) {
                result = backupService.restoreBackup(inputStream);
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    result.summary());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Database restore failed: " + e.getMessage());
        }

        return "redirect:/admin/backup";
    }

    @PostMapping(value = "/restore", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RestoreResponse> restoreBackupJson(HttpServletRequest request) {
        if (request.getContentLengthLong() > MAX_BACKUP_SIZE_BYTES) {
            return ResponseEntity.status(413)
                    .body(new RestoreResponse(false, "Backup exceeds the 100 MiB restore limit."));
        }
        try (InputStream inputStream = new SizeLimitedInputStream(request.getInputStream(), MAX_BACKUP_SIZE_BYTES)) {
            BackupService.RestoreResult result = backupService.restoreBackup(inputStream);
            return ResponseEntity.ok(new RestoreResponse(true, result.summary()));
        } catch (Exception e) {
            if (hasSizeLimitCause(e)) {
                return ResponseEntity.status(413)
                        .body(new RestoreResponse(false, "Backup exceeds the 100 MiB restore limit."));
            }
            return ResponseEntity.badRequest()
                    .body(new RestoreResponse(false, "Database restore failed: " + e.getMessage()));
        }
    }

    private boolean hasSizeLimitCause(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof BackupSizeLimitException) return true;
        }
        return false;
    }

    public record RestoreResponse(boolean success, String message) {
    }

    private static final class SizeLimitedInputStream extends FilterInputStream {
        private long remaining;

        private SizeLimitedInputStream(InputStream inputStream, long maxBytes) {
            super(inputStream);
            remaining = maxBytes;
        }

        @Override
        public int read() throws IOException {
            if (remaining == 0) {
                if (super.read() == -1) return -1;
                throw new BackupSizeLimitException();
            }
            int value = super.read();
            if (value != -1) remaining--;
            return value;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (length == 0) return 0;
            if (remaining == 0) {
                if (super.read() == -1) return -1;
                throw new BackupSizeLimitException();
            }
            int read = super.read(buffer, offset, (int) Math.min(length, remaining));
            if (read > 0) remaining -= read;
            return read;
        }
    }

    private static final class BackupSizeLimitException extends IOException {
        private BackupSizeLimitException() {
            super("Backup exceeds the configured restore limit.");
        }
    }
}
