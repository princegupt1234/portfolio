package com.example.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Pulls basic LeetCode stats via the public leetcode-stats-api mirror for the
 * "LeetCode" section. Requires outbound internet access at runtime; falls back
 * to zeros if the third-party API is unreachable.
 */
@Service
public class LeetCodeStatsService {

    private final RestClient restClient = RestClient.create();

    public record LeetCodeStats(int totalSolved, int easySolved, int mediumSolved, int hardSolved,
                                 double ranking, boolean available) {
    }

    @SuppressWarnings("unchecked")
    public LeetCodeStats fetchStats(String username) {
        if (username == null || username.isBlank()) {
            return new LeetCodeStats(0, 0, 0, 0, 0, false);
        }
        try {
            Map<String, Object> body = restClient.get()
                    .uri("https://leetcode-stats-api.herokuapp.com/{username}", username)
                    .retrieve()
                    .body(Map.class);
            if (body == null || !"success".equals(body.get("status"))) {
                return new LeetCodeStats(0, 0, 0, 0, 0, false);
            }
            int total = ((Number) body.getOrDefault("totalSolved", 0)).intValue();
            int easy = ((Number) body.getOrDefault("easySolved", 0)).intValue();
            int medium = ((Number) body.getOrDefault("mediumSolved", 0)).intValue();
            int hard = ((Number) body.getOrDefault("hardSolved", 0)).intValue();
            double ranking = body.get("ranking") == null ? 0 : ((Number) body.get("ranking")).doubleValue();
            return new LeetCodeStats(total, easy, medium, hard, ranking, true);
        } catch (Exception e) {
            return new LeetCodeStats(0, 0, 0, 0, 0, false);
        }
    }
}
