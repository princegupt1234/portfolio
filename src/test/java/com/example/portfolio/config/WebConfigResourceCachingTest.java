package com.example.portfolio.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class WebConfigResourceCachingTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    private final List<Path> temporaryFiles = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void removeTemporaryFiles() throws IOException {
        for (Path file : temporaryFiles) {
            Files.deleteIfExists(file);
        }
    }

    private Path createUpload(String subfolder, String content) throws IOException {
        Path file = Path.of(uploadDirectory, subfolder, "cache-test-" + UUID.randomUUID() + ".txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        temporaryFiles.add(file);
        return file;
    }

    @Test
    void publicProfileUploadsAreCachedForOneDayAndCanBeRevalidated() throws Exception {
        Path file = createUpload("profile", "profile image content");
        var response = mockMvc.perform(get("/uploads/profile/" + file.getFileName()))
                .andExpect(status().isOk())
                .andExpect(content().string("profile image content"))
                .andReturn()
                .getResponse();

        String cacheControl = response.getHeader(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("public"));
        assertTrue(cacheControl.contains("max-age=" + Duration.ofDays(1).getSeconds()));
        assertFalse(cacheControl.contains("no-cache"));

        ZonedDateTime lastModified = ZonedDateTime.parse(
                response.getHeader(HttpHeaders.LAST_MODIFIED), DateTimeFormatter.RFC_1123_DATE_TIME);
        mockMvc.perform(get("/uploads/profile/" + file.getFileName())
                        .ifModifiedSince(lastModified))
                .andExpect(status().isNotModified())
                .andExpect(content().string(""));
    }

    @Test
    void otherUploadsDoNotReceiveThePublicProfileCachePolicy() throws Exception {
        Path file = createUpload("admin", "admin image content");
        var response = mockMvc.perform(get("/uploads/admin/" + file.getFileName()))
                .andExpect(status().isOk())
                .andExpect(content().string("admin image content"))
                .andReturn()
                .getResponse();

        String cacheControl = response.getHeader(HttpHeaders.CACHE_CONTROL);
        assertFalse(cacheControl != null && cacheControl.contains("public"));
    }
}
