package com.example.portfolio.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeetCodeRepositoryStatsServiceTest {

    private final LeetCodeRepositoryStatsService service = new LeetCodeRepositoryStatsService();

    @Test
    void parsesLeetCodeGraphQLResponseSuccessfully() {
        String json = """
                {"data":{"matchedUser":{"submitStatsGlobal":{"acSubmissionNum":[{"difficulty":"All","count":7,"submissions":8},{"difficulty":"Easy","count":5,"submissions":6},{"difficulty":"Medium","count":2,"submissions":2},{"difficulty":"Hard","count":0,"submissions":0}]}}}}
                """;

        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats = service.parseResponse(json);

        assertTrue(stats.available());
        assertEquals(7, stats.solved());
        assertEquals(5, stats.easy());
        assertEquals(2, stats.medium());
        assertEquals(0, stats.hard());
    }

    @Test
    void parsesLeetCodeRestProxyResponseSuccessfully() {
        String json = """
                {"totalSolved":7,"totalQuestions":3361,"easySolved":5,"totalEasy":836,"mediumSolved":2,"totalMedium":1745,"hardSolved":0,"totalHard":780}
                """;

        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats = service.parseResponse(json);

        assertTrue(stats.available());
        assertEquals(7, stats.solved());
        assertEquals(5, stats.easy());
        assertEquals(2, stats.medium());
        assertEquals(0, stats.hard());
    }

    @Test
    void computesSolvedFromEasyMediumHardWhenSolvedKeyIsAbsent() {
        String json = """
                {"leetcode":{"easy":3,"hard":1,"medium":2,"shas":{}}}
                """;

        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats = service.parseResponse(json);

        assertTrue(stats.available());
        assertEquals(3, stats.easy());
        assertEquals(2, stats.medium());
        assertEquals(1, stats.hard());
        assertEquals(6, stats.solved(), "Solved count should be sum of easy, medium, and hard");
    }

    @Test
    void usesExplicitSolvedWhenPresentAndGreaterThanZero() {
        String json = """
                {"leetcode":{"solved":25,"easy":10,"hard":5,"medium":10}}
                """;

        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats = service.parseResponse(json);

        assertTrue(stats.available());
        assertEquals(10, stats.easy());
        assertEquals(10, stats.medium());
        assertEquals(5, stats.hard());
        assertEquals(25, stats.solved());
    }

    @Test
    void returnsUnavailableWhenResponseIsMalformedOrMissingLeetcode() {
        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats1 = service.parseResponse(null);
        assertFalse(stats1.available());
        assertEquals(0, stats1.solved());

        LeetCodeRepositoryStatsService.LeetCodeRepositoryStats stats2 = service.parseResponse("{\"status\":\"error\"}");
        assertFalse(stats2.available());
        assertEquals(0, stats2.solved());
    }
}
