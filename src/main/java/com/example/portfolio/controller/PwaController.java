package com.example.portfolio.controller;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PwaController {

    private final AboutInfoRepository aboutInfoRepository;

    public PwaController(AboutInfoRepository aboutInfoRepository) {
        this.aboutInfoRepository = aboutInfoRepository;
    }

    @GetMapping(value = {"/manifest.webmanifest", "/manifest.json"}, produces = "application/manifest+json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getManifest() {
        AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElseGet(AboutInfo::new);

        String name = (about.getPwaAppName() != null && !about.getPwaAppName().isBlank())
                ? about.getPwaAppName()
                : (about.getFullName() != null ? about.getFullName() + " | Portfolio" : "Prince Gupt | Portfolio");

        String shortName = (about.getPwaShortName() != null && !about.getPwaShortName().isBlank())
                ? about.getPwaShortName()
                : "Prince Portfolio";

        String themeColor = (about.getPwaThemeColor() != null && !about.getPwaThemeColor().isBlank())
                ? about.getPwaThemeColor()
                : "#0a0f1d";

        String bgColor = (about.getPwaBackgroundColor() != null && !about.getPwaBackgroundColor().isBlank())
                ? about.getPwaBackgroundColor()
                : "#060913";

        Map<String, Object> manifest = Map.of(
                "name", name,
                "short_name", shortName,
                "description", about.getBio() != null ? about.getBio() : "Prince Gupt - Full Stack Java & Spring Boot Developer Portfolio",
                "start_url", "/",
                "scope", "/",
                "display", "standalone",
                "orientation", "portrait-primary",
                "theme_color", themeColor,
                "background_color", bgColor,
                "icons", List.of(
                        Map.of(
                                "src", "/icons/icon-192.png",
                                "sizes", "192x192",
                                "type", "image/png",
                                "purpose", "any maskable"
                        ),
                        Map.of(
                                "src", "/icons/icon-512.png",
                                "sizes", "512x512",
                                "type", "image/png",
                                "purpose", "any maskable"
                        ),
                        Map.of(
                                "src", "/icons/favicon.svg",
                                "sizes", "any",
                                "type", "image/svg+xml"
                        )
                )
        );

        return ResponseEntity.ok(manifest);
    }
}
