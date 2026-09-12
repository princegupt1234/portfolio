package com.example.portfolio.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class ContactRateLimiterServiceTest {

    private ContactRateLimiterService rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new ContactRateLimiterService();
    }

    @Test
    void allowsFirstRequestAndBlocksRapidSubsequentRequest() {
        String ip = "192.168.1.50";

        // First attempt should succeed
        assertTrue(rateLimiter.checkAndRecord(ip, 60));

        // Immediate second attempt within 60s cooldown should be blocked
        assertFalse(rateLimiter.checkAndRecord(ip, 60));
    }

    @Test
    void allowsRequestsWhenCooldownIsZeroOrDisabled() {
        String ip = "10.0.0.1";

        assertTrue(rateLimiter.checkAndRecord(ip, 0));
        assertTrue(rateLimiter.checkAndRecord(ip, 0));
    }

    @Test
    void differentIpsDoNotBlockEachOther() {
        String ip1 = "203.0.113.1";
        String ip2 = "203.0.113.2";

        assertTrue(rateLimiter.checkAndRecord(ip1, 60));
        assertTrue(rateLimiter.checkAndRecord(ip2, 60));
    }

    @Test
    void extractsFirstIpFromForwardedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "49.36.120.15, 10.0.0.1");

        assertEquals("49.36.120.15", rateLimiter.extractClientIp(request));
    }

    @Test
    void extractsRealIpWhenNoForwardedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "182.74.5.10");

        assertEquals("182.74.5.10", rateLimiter.extractClientIp(request));
    }
}
