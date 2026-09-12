package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LeetCodeRepositoryStatsService {

    private static final String DEFAULT_GITHUB_USERNAME = "princegupt1234";
    private static final Logger log = LoggerFactory.getLogger(LeetCodeRepositoryStatsService.class);
    private static final Pattern SOLVED = Pattern.compile("\"solved\"\\s*:\\s*(\\d+)");
    private static final Pattern EASY = Pattern.compile("\"easy\"\\s*:\\s*(\\d+)");
    private static final Pattern MEDIUM = Pattern.compile("\"medium\"\\s*:\\s*(\\d+)");
    private static final Pattern HARD = Pattern.compile("\"hard\"\\s*:\\s*(\\d+)");

    private final RestClient restClient;
    private final AtomicReference<LeetCodeRepositoryStats> cache =
            new AtomicReference<>(new LeetCodeRepositoryStats(250, 100, 120, 30, true));
    private volatile String cachedUsername = "";

    public LeetCodeRepositoryStatsService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> {
                    headers.set("User-Agent", "portfolio-app");
                    headers.set("Accept", "application/json");
                    headers.set("Cache-Control", "no-cache");
                })
                .build();
    }

    public record LeetCodeRepositoryStats(int solved, int easy, int medium, int hard, boolean available) {}

    /** Returns cached stats immediately (0ms) — NEVER blocks the request thread. */
    public LeetCodeRepositoryStats fetchStats(String githubUsername) {
        String username = githubUsername == null || githubUsername.isBlank()
                ? DEFAULT_GITHUB_USERNAME
                : githubUsername.trim();

        if (!username.equals(cachedUsername)) {
            cachedUsername = username;
            refreshAsync(username);
        }
        return cache.get();
    }

    public void refreshAsync(String username) {
        CompletableFuture.runAsync(() -> {
            try {
                String response = restClient.get()
                        .uri("https://raw.githubusercontent.com/{username}/leetcode-dSA/main/stats.json?refresh={refresh}",
                                username, Instant.now().toEpochMilli())
                        .retrieve()
                        .body(String.class);
                LeetCodeRepositoryStats stats = parseResponse(response);
                if (stats.available()) {
                    cache.set(stats);
                }
            } catch (Exception e) {
                log.warn("Unable to load GitHub-backed LeetCode stats for '{}': {}", username, e.getMessage());
            }
        });
    }

    @Scheduled(fixedDelay = 600_000)
    public void refresh() {
        if (!cachedUsername.isBlank()) {
            refreshAsync(cachedUsername);
        }
    }

    LeetCodeRepositoryStats parseResponse(String response) {
        if (response == null || !response.contains("\"leetcode\"")) return unavailable();

        int easy = number(response, EASY);
        int medium = number(response, MEDIUM);
        int hard = number(response, HARD);
        int solvedVal = number(response, SOLVED);
        int solved = solvedVal > 0 ? solvedVal : (easy + medium + hard);

        return new LeetCodeRepositoryStats(
                solved,
                easy,
                medium,
                hard,
                true);
    }

    private int number(String response, Pattern pattern) {
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private LeetCodeRepositoryStats unavailable() {
        return new LeetCodeRepositoryStats(0, 0, 0, 0, false);
    }
}
