package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
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
    private final RestClient restClient = RestClient.builder()
            .defaultHeaders(headers -> {
                headers.set("User-Agent", "portfolio-app");
                headers.set("Accept", "application/json");
                headers.set("Cache-Control", "no-cache");
            })
            .build();

    public record LeetCodeRepositoryStats(int solved, int easy, int medium, int hard, boolean available) {}

    public LeetCodeRepositoryStats fetchStats(String githubUsername) {
        String username = githubUsername == null || githubUsername.isBlank()
                ? DEFAULT_GITHUB_USERNAME
                : githubUsername.trim();

        try {
            String response = restClient.get()
                    .uri("https://raw.githubusercontent.com/{username}/leetcode-dSA/main/stats.json?refresh={refresh}",
                        username, Instant.now().toEpochMilli())
                    .retrieve()
                    .body(String.class);
            if (response == null || !response.contains("\"leetcode\"")) return unavailable();

            return new LeetCodeRepositoryStats(
                    number(response, SOLVED),
                    number(response, EASY),
                    number(response, MEDIUM),
                    number(response, HARD),
                    true);
        } catch (Exception e) {
            log.warn("Unable to load GitHub-backed LeetCode stats for '{}': {}", username, e.getMessage());
            return unavailable();
        }
    }

    private int number(String response, Pattern pattern) {
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private LeetCodeRepositoryStats unavailable() {
        return new LeetCodeRepositoryStats(0, 0, 0, 0, false);
    }
}
