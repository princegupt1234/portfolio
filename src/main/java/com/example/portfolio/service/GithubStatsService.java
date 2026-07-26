package com.example.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Pulls basic public GitHub stats (followers, public repo count, etc.) for the
 * "GitHub Section" of the portfolio. Requires outbound internet access at runtime
 * (this calls api.github.com) - if the call fails or times out, sensible
 * fallback zeros are returned instead of breaking the page.
 */
@Service
public class GithubStatsService {

    private final RestClient restClient = RestClient.create();

    public record GithubStats(int publicRepos, int followers, int following, String avatarUrl, boolean available) {
    }

    @SuppressWarnings("unchecked")
    public GithubStats fetchStats(String username) {
        if (username == null || username.isBlank()) {
            return new GithubStats(0, 0, 0, null, false);
        }
        try {
            Map<String, Object> body = restClient.get()
                    .uri("https://api.github.com/users/{username}", username)
                    .retrieve()
                    .body(Map.class);
            if (body == null) {
                return new GithubStats(0, 0, 0, null, false);
            }
            int repos = ((Number) body.getOrDefault("public_repos", 0)).intValue();
            int followers = ((Number) body.getOrDefault("followers", 0)).intValue();
            int following = ((Number) body.getOrDefault("following", 0)).intValue();
            String avatar = (String) body.get("avatar_url");
            return new GithubStats(repos, followers, following, avatar, true);
        } catch (Exception e) {
            return new GithubStats(0, 0, 0, null, false);
        }
    }
}
