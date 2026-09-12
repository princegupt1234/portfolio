package com.example.portfolio.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ContactRateLimiterService {

    private final Map<String, Instant> ipSubmissionMap = new ConcurrentHashMap<>();

    /**
     * Checks whether the client IP is allowed to submit a message.
     * If allowed, updates the last submission timestamp.
     *
     * @param clientIp        The IP address of the client
     * @param cooldownSeconds Cooldown duration in seconds (if <= 0, rate limiting is disabled)
     * @return true if submission is allowed, false if rate limited
     */
    public boolean checkAndRecord(String clientIp, int cooldownSeconds) {
        if (cooldownSeconds <= 0 || clientIp == null || clientIp.isBlank()) {
            return true;
        }

        cleanOldEntries();

        Instant now = Instant.now();
        Instant lastSubmission = ipSubmissionMap.get(clientIp);

        if (lastSubmission != null) {
            long elapsedSeconds = Duration.between(lastSubmission, now).getSeconds();
            if (elapsedSeconds < cooldownSeconds) {
                return false;
            }
        }

        ipSubmissionMap.put(clientIp, now);
        return true;
    }

    /**
     * Extracts client IP address with support for reverse proxies (Render, Cloudflare, etc.).
     */
    public String extractClientIp(HttpServletRequest request) {
        if (request == null) return "unknown";

        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // "client, proxy1, proxy2" -> extract first IP
            String[] ips = forwarded.split(",");
            if (ips.length > 0 && !ips[0].trim().isBlank()) {
                return ips[0].trim();
            }
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    /**
     * Purges entries older than 1 hour to prevent map bloat.
     */
    private void cleanOldEntries() {
        if (ipSubmissionMap.size() > 500) {
            Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
            ipSubmissionMap.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
        }
    }

    public void clear() {
        ipSubmissionMap.clear();
    }
}
