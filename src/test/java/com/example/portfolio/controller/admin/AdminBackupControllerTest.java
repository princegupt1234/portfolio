package com.example.portfolio.controller.admin;

import com.example.portfolio.service.BackupService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdminBackupControllerTest {

    @Test
    void downloadsBackupUsingStableRequiredFilename() {
        BackupService backupService = mock(BackupService.class);
        when(backupService.exportBackupBytes()).thenReturn("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var response = new AdminBackupController(backupService).exportBackup();

        assertEquals("attachment; filename=\"portfolio-backup.json\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    void acceptsBoundedRawJsonRestoreRequests() {
        BackupService backupService = mock(BackupService.class);
        when(backupService.restoreBackup(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new BackupService.RestoreResult(java.util.Map.of("projects", 1), java.util.Map.of()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var response = new AdminBackupController(backupService).restoreBackupJson(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody().success());
    }

    @Test
    void rejectsRawJsonRestoreRequestsAboveSizeLimitBeforeReadingThem() {
        BackupService backupService = mock(BackupService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContentLengthLong()).thenReturn(100L * 1024 * 1024 + 1);

        var response = new AdminBackupController(backupService).restoreBackupJson(request);

        assertEquals(413, response.getStatusCode().value());
        verifyNoInteractions(backupService);
    }
}
