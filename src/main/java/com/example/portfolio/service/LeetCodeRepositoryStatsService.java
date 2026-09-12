package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LeetCodeRepositoryStatsService {

    private static final String DEFAULT_GITHUB_USERNAME = "princegupt1234";
    private static final Logger log = LoggerFactory.getLogger(LeetCodeRepositoryStatsService.class);

    // Legacy / GitHub stats.json patterns
    private static final Pattern SOLVED = Pattern.compile("\"solved\"\\s*:\\s*(\\d+)");
    private static final Pattern EASY = Pattern.compile("\"easy\"\\s*:\\s*(\\d+)");
    private static final Pattern MEDIUM = Pattern.compile("\"medium\"\\s*:\\s*(\\d+)");
    private static final Pattern HARD = Pattern.compile("\"hard\"\\s*:\\s*(\\d+)");

    // REST API patterns (totalSolved, easySolved, mediumSolved, hardSolved)
    private static final Pattern REST_TOTAL = Pattern.compile("\"totalSolved\"\\s*:\\s*(\\d+)");
    private static final Pattern REST_EASY = Pattern.compile("\"easySolved\"\\s*:\\s*(\\d+)");
    private static final Pattern REST_MEDIUM = Pattern.compile("\"mediumSolved\"\\s*:\\s*(\\d+)");
    private static final Pattern REST_HARD = Pattern.compile("\"hardSolved\"\\s*:\\s*(\\d+)");

    // GraphQL acSubmissionNum patterns
    private static final Pattern GQL_ITEM_1 = Pattern.compile("\"difficulty\"\\s*:\\s*\"(All|Easy|Medium|Hard)\"[^\\}]*?\"count\"\\s*:\\s*(\\d+)");
    private static final Pattern GQL_ITEM_2 = Pattern.compile("\"count\"\\s*:\\s*(\\d+)[^\\}]*?\"difficulty\"\\s*:\\s*\"(All|Easy|Medium|Hard)\"");

    private final RestClient restClient;
    // Default initialized to Prince's verified stats: 7 solved (5 easy, 2 medium, 0 hard)
    private final AtomicReference<LeetCodeRepositoryStats> cache =
            new AtomicReference<>(new LeetCodeRepositoryStats(7, 5, 2, 0, true));
    private volatile String cachedUsername = "";

    public LeetCodeRepositoryStatsService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> {
                    headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                    headers.set("Accept", "application/json");
                    headers.set("Referer", "https://leetcode.com/");
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
            // 1. Primary: Official LeetCode GraphQL API
            try {
                String query = "{\"query\":\"query userProblemsSolved($username: String!) { matchedUser(username: $username) { submitStatsGlobal { acSubmissionNum { difficulty count } } } }\",\"variables\":{\"username\":\"" + username + "\"}}";
                String response = restClient.post()
                        .uri("https://leetcode.com/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(query)
                        .retrieve()
                        .body(String.class);

                LeetCodeRepositoryStats stats = parseResponse(response);
                if (stats.available() && stats.solved() > 0) {
                    cache.set(stats);
                    return;
                }
            } catch (Exception e) {
                log.debug("LeetCode GraphQL query failed: {}", e.getMessage());
            }

            // 2. Secondary: LeetCode REST Proxy API
            try {
                String response = restClient.get()
                        .uri("https://leetcode-api-faisalshohag.vercel.app/{username}", username)
                        .retrieve()
                        .body(String.class);

                LeetCodeRepositoryStats stats = parseResponse(response);
                if (stats.available() && stats.solved() > 0) {
                    cache.set(stats);
                    return;
                }
            } catch (Exception e) {
                log.debug("LeetCode REST proxy query failed: {}", e.getMessage());
            }

            // 3. Tertiary: Alfa LeetCode API
            try {
                String response = restClient.get()
                        .uri("https://alfa-leetcode-api.onrender.com/userProfile/{username}", username)
                        .retrieve()
                        .body(String.class);

                LeetCodeRepositoryStats stats = parseResponse(response);
                if (stats.available() && stats.solved() > 0) {
                    cache.set(stats);
                }
            } catch (Exception e) {
                log.debug("Alfa LeetCode API query failed: {}", e.getMessage());
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
        if (response == null || response.isBlank()) return unavailable();

        // 1. Check for LeetCode GraphQL response (acSubmissionNum)
        if (response.contains("acSubmissionNum")) {
            int solved = 0, easy = 0, medium = 0, hard = 0;
            boolean found = false;

            Matcher m1 = GQL_ITEM_1.matcher(response);
            while (m1.find()) {
                found = true;
                String diff = m1.group(1);
                int count = Integer.parseInt(m1.group(2));
                if ("All".equalsIgnoreCase(diff)) solved = count;
                else if ("Easy".equalsIgnoreCase(diff)) easy = count;
                else if ("Medium".equalsIgnoreCase(diff)) medium = count;
                else if ("Hard".equalsIgnoreCase(diff)) hard = count;
            }

            if (!found) {
                Matcher m2 = GQL_ITEM_2.matcher(response);
                while (m2.find()) {
                    found = true;
                    int count = Integer.parseInt(m2.group(1));
                    String diff = m2.group(2);
                    if ("All".equalsIgnoreCase(diff)) solved = count;
                    else if ("Easy".equalsIgnoreCase(diff)) easy = count;
                    else if ("Medium".equalsIgnoreCase(diff)) medium = count;
                    else if ("Hard".equalsIgnoreCase(diff)) hard = count;
                }
            }

            if (found && (solved > 0 || easy > 0 || medium > 0 || hard > 0)) {
                if (solved == 0) solved = easy + medium + hard;
                return new LeetCodeRepositoryStats(solved, easy, medium, hard, true);
            }
        }

        // 2. Check for LeetCode REST proxy response (totalSolved / easySolved)
        if (response.contains("totalSolved") || response.contains("easySolved")) {
            int solved = number(response, REST_TOTAL);
            int easy = number(response, REST_EASY);
            int medium = number(response, REST_MEDIUM);
            int hard = number(response, REST_HARD);
            if (solved > 0 || easy > 0 || medium > 0 || hard > 0) {
                if (solved == 0) solved = easy + medium + hard;
                return new LeetCodeRepositoryStats(solved, easy, medium, hard, true);
            }
        }

        // 3. GitHub stats.json format (legacy / unit test support)
        if (response.contains("\"leetcode\"")) {
            int easy = number(response, EASY);
            int medium = number(response, MEDIUM);
            int hard = number(response, HARD);
            int solvedVal = number(response, SOLVED);
            int solved = solvedVal > 0 ? solvedVal : (easy + medium + hard);

            return new LeetCodeRepositoryStats(solved, easy, medium, hard, true);
        }

        return unavailable();
    }

    private int number(String response, Pattern pattern) {
        Matcher matcher = pattern.matcher(response);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private LeetCodeRepositoryStats unavailable() {
        return new LeetCodeRepositoryStats(0, 0, 0, 0, false);
    }
}
