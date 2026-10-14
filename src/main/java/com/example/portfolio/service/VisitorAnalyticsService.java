package com.example.portfolio.service;

import com.example.portfolio.entity.VisitorClassification;
import com.example.portfolio.entity.VisitorClassificationRecord;
import com.example.portfolio.repository.VisitorClassificationRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class VisitorAnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(VisitorAnalyticsService.class);
    private static final String VISITOR_COOKIE = "portfolio_visitor";
    private static final int VISITOR_COOKIE_MAX_AGE_SECONDS = 30 * 24 * 60 * 60;
    private static final Pattern VISITOR_TOKEN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-8][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");
    private static final Pattern BOT_AGENT = Pattern.compile(
            "googlebot|bingbot|duckduckbot|baiduspider|yandexbot|google-inspectiontool|crawler|spider|slurp|" +
                    "headlesschrome|phantomjs|selenium|puppeteer|playwright|(?:^|[\\s/;(])bot(?:$|[\\s/);])|" +
                    "facebookexternalhit|twitterbot|linkedinbot|discordbot|slackbot|telegrambot|whatsapp|" +
                    "applebot|bingpreview|ahrefsbot|semrushbot|mj12bot|bytespider|gptbot|claudebot|perplexitybot",
            Pattern.CASE_INSENSITIVE);

    private final VisitorClassificationRepository repository;
    private final byte[] botIdentitySecret = createSecret();

    public VisitorAnalyticsService(VisitorClassificationRepository repository) {
        this.repository = repository;
    }

    public VisitIdentity capture(HttpServletRequest request, HttpServletResponse response) {
        LocalDate today = LocalDate.now();
        String userAgent = request.getHeader("User-Agent");
        VisitorClassification classification = classify(userAgent);
        String visitorKey;

        if (classification == VisitorClassification.BOT) {
            visitorKey = hashBotIdentity(request.getRemoteAddr(), userAgent);
        } else {
            String token = readVisitorToken(request).orElseGet(() -> {
                String generated = UUID.randomUUID().toString();
                Cookie cookie = new Cookie(VISITOR_COOKIE, generated);
                cookie.setHttpOnly(true);
                cookie.setSecure(request.isSecure());
                cookie.setPath("/");
                cookie.setMaxAge(VISITOR_COOKIE_MAX_AGE_SECONDS);
                cookie.setAttribute("SameSite", "Lax");
                response.addCookie(cookie);
                return generated;
            });
            visitorKey = sha256(token);
        }
        return new VisitIdentity(today, visitorKey, classification);
    }

    @Transactional
    public synchronized void record(VisitIdentity identity) {
        Optional<VisitorClassificationRecord> existing = repository.findByVisitDateAndVisitorKeyHash(
                identity.visitDate(), identity.visitorKeyHash());
        if (existing.isPresent()) {
            VisitorClassificationRecord record = existing.get();
            if (record.getClassification() != VisitorClassification.BOT
                    && identity.classification() == VisitorClassification.BOT) {
                record.setClassification(VisitorClassification.BOT);
                repository.save(record);
            } else if (record.getClassification() == VisitorClassification.UNKNOWN
                    && identity.classification() == VisitorClassification.HUMAN_LIKELY) {
                record.setClassification(VisitorClassification.HUMAN_LIKELY);
                repository.save(record);
            }
            return;
        }

        VisitorClassificationRecord record = new VisitorClassificationRecord();
        record.setVisitDate(identity.visitDate());
        record.setVisitorKeyHash(identity.visitorKeyHash());
        record.setClassification(identity.classification());
        repository.save(record);
    }

    @Transactional(readOnly = true)
    public TrafficSummary summary() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(29);
        return new TrafficSummary(
                repository.countByVisitDateAndClassification(today, VisitorClassification.HUMAN_LIKELY),
                repository.countByVisitDateAndClassification(today, VisitorClassification.BOT),
                repository.countUniqueVisitors(start, today, VisitorClassification.HUMAN_LIKELY),
                repository.countUniqueVisitors(start, today, VisitorClassification.BOT),
                repository.findFirstTrackedDate());
    }

    public static VisitorClassification classify(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return VisitorClassification.UNKNOWN;
        if (BOT_AGENT.matcher(userAgent).find()) return VisitorClassification.BOT;
        return VisitorClassification.HUMAN_LIKELY;
    }

    private Optional<String> readVisitorToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        for (Cookie cookie : cookies) {
            if (VISITOR_COOKIE.equals(cookie.getName())
                    && VISITOR_TOKEN.matcher(cookie.getValue()).matches()) {
                return Optional.of(cookie.getValue().toLowerCase());
            }
        }
        return Optional.empty();
    }

    private String hashBotIdentity(String remoteAddress, String userAgent) {
        String material = (remoteAddress == null ? "" : remoteAddress) + "\n" + userAgent;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(botIdentitySecret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(material.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            log.error("Could not create a privacy-preserving bot visitor key.", e);
            throw new IllegalStateException("Visitor tracking is unavailable.", e);
        }
    }

    private String sha256(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            log.error("Could not hash an anonymous visitor token.", e);
            throw new IllegalStateException("Visitor tracking is unavailable.", e);
        }
    }

    private byte[] createSecret() {
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        return secret;
    }

    public record VisitIdentity(LocalDate visitDate, String visitorKeyHash,
                                VisitorClassification classification) {
    }

    public record TrafficSummary(long humanToday, long botsToday, long human30Days,
                                 long bots30Days, LocalDate trackingStarted) {
        public boolean available() {
            return trackingStarted != null;
        }
    }
}
