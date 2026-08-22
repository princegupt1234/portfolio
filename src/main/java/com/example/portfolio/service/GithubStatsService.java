package com.example.portfolio.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GithubStatsService {

    private final RestClient restClient = RestClient.builder()
            .requestInterceptor((req, body, exec) -> {
                req.getHeaders().set("User-Agent", "portfolio-app");
                return exec.execute(req, body);
            })
            .build();

    public record GithubStats(int publicRepos, int followers, int following, boolean available) {}

    private volatile String cachedUsername = "";
    private final AtomicReference<GithubStats> cache = new AtomicReference<>(new GithubStats(0, 0, 0, false));

    /** Returns cached value immediately — never blocks the request thread. */
    public GithubStats fetchStats(String username) {
        if (username == null || username.isBlank()) return new GithubStats(0, 0, 0, false);
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
    private GithubStats loadFromApi(String username) {
        try {
            Map<String, Object> body = restClient.get()
                    .uri("https://api.github.com/users/{username}", username)
                    .retrieve().body(Map.class);
            if (body == null) return new GithubStats(0, 0, 0, false);
            return new GithubStats(
                    ((Number) body.getOrDefault("public_repos", 0)).intValue(),
                    ((Number) body.getOrDefault("followers", 0)).intValue(),
                    ((Number) body.getOrDefault("following", 0)).intValue(),
                    true);
        } catch (Exception e) {
            return new GithubStats(0, 0, 0, false);
        }
    }
}
