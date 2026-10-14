package com.example.portfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.time.Duration;

@Configuration
@EnableAsync
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Path.of(uploadDir).toAbsolutePath();
        try {
            java.nio.file.Files.createDirectories(path);
        } catch (java.io.IOException ignored) {
        }
        String location = path.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        String profileLocation = path.resolve("profile").toUri().toString();
        if (!profileLocation.endsWith("/")) {
            profileLocation += "/";
        }
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations(profileLocation)
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic());
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
