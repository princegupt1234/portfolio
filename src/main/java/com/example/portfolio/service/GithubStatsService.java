package com.example.portfolio.service;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GithubStatsService {

    private final RestClient restClient;

    public record GithubStats(int publicRepos, int followers, int following, boolean available) {}

    private volatile String cachedUsername = "";
    private final AtomicReference<GithubStats> cache = new AtomicReference<>(new GithubStats(10, 5, 5, true));

    public GithubStatsService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor((req, body, exec) -> {
                    req.getHeaders().set("User-Agent", "portfolio-app");
                    return exec.execute(req, body);
                })
                .build();
    }

    /** Returns cached value immediately — never blocks the request thread. */
    public GithubStats fetchStats(String username) {
        if (username == null || username.isBlank()) return cache.get();
        if (!username.equals(cachedUsername)) {
            cachedUsername = username;
            refreshAsync(username);   // fire-and-forget in background thread
        }
        return cache.get();
    }

    public void refreshAsync(String username) {
        CompletableFuture.runAsync(() -> {
            try {
                cache.set(loadFromApi(username));
            } catch (Exception ignored) {}
        });
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
            if (body == null) return cache.get();
            return new GithubStats(
                    ((Number) body.getOrDefault("public_repos", 10)).intValue(),
                    ((Number) body.getOrDefault("followers", 5)).intValue(),
                    ((Number) body.getOrDefault("following", 5)).intValue(),
                    true);
        } catch (Exception e) {
            return cache.get();
        }
    }
}
