package com.example.portfolio.service;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LeetCodeStatsService {

    private static final Logger log = LoggerFactory.getLogger(LeetCodeStatsService.class);
    private final RestClient restClient = RestClient.builder()
            .defaultHeaders(headers -> {
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/131.0.0.0 Safari/537.36");
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
                headers.set("Accept-Language", "en-US,en;q=0.9");
            })
            .build();
    private static final String GRAPHQL_URL = "https://leetcode.com/graphql";
    private static final String QUERY = """
            {"query":"query getUserProfile($username: String!) { matchedUser(username: $username) { submitStats { acSubmissionNum { difficulty count } } profile { ranking } } }","variables":{"username":"%s"}}
            """;

    public record LeetCodeStats(int totalSolved, int easySolved, int mediumSolved, int hardSolved,
                                 int ranking, boolean available) {}

    private volatile String cachedUsername = "";
    private final AtomicReference<LeetCodeStats> cache = new AtomicReference<>(new LeetCodeStats(0, 0, 0, 0, 0, false));

    /** Returns cached value immediately — never blocks the request thread. */
    public LeetCodeStats fetchStats(String username) {
        if (username == null || username.isBlank()) return new LeetCodeStats(0, 0, 0, 0, 0, false);
        if (!username.equals(cachedUsername)) {
            cachedUsername = username;
            refreshAsync(username);   // fire-and-forget
        }
        return cache.get();
    }

    @Async
    public void refreshAsync(String username) {
        cache.set(loadFromApi(username));
    }

    @Scheduled(fixedDelay = 600_000)
    public void refresh() {
        if (!cachedUsername.isBlank()) refreshAsync(cachedUsername);
    }

    @SuppressWarnings("unchecked")
    private LeetCodeStats loadFromApi(String username) {
        try {
            Map<String, Object> response = restClient.post()
                    .uri(GRAPHQL_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Referer", "https://leetcode.com")
                        .header("Origin", "https://leetcode.com")
                    .body(QUERY.formatted(username).strip())
                    .retrieve().body(Map.class);
            if (response == null) return new LeetCodeStats(0, 0, 0, 0, 0, false);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data == null) return new LeetCodeStats(0, 0, 0, 0, 0, false);
            Map<String, Object> matchedUser = (Map<String, Object>) data.get("matchedUser");
            if (matchedUser == null) return new LeetCodeStats(0, 0, 0, 0, 0, false);
            Map<String, Object> submitStats = (Map<String, Object>) matchedUser.get("submitStats");
                    if (submitStats == null) return new LeetCodeStats(0, 0, 0, 0, 0, false);
            List<Map<String, Object>> acList = (List<Map<String, Object>>) submitStats.get("acSubmissionNum");
                    if (acList == null) return new LeetCodeStats(0, 0, 0, 0, 0, false);
            int total = 0, easy = 0, medium = 0, hard = 0;
            for (Map<String, Object> entry : acList) {
                int count = ((Number) entry.get("count")).intValue();
                switch ((String) entry.get("difficulty")) {
                    case "All" -> total = count;
                    case "Easy" -> easy = count;
                    case "Medium" -> medium = count;
                    case "Hard" -> hard = count;
                }
            }
            Map<String, Object> profile = (Map<String, Object>) matchedUser.get("profile");
            int ranking = profile.get("ranking") == null ? 0 : ((Number) profile.get("ranking")).intValue();
            return new LeetCodeStats(total, easy, medium, hard, ranking, true);
        } catch (Exception e) {
            log.warn("Unable to load LeetCode stats for username '{}': {}", username, e.getMessage());
            return new LeetCodeStats(0, 0, 0, 0, 0, false);
        }
    }
}
