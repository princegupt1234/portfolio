package com.example.portfolio.service;

import com.example.portfolio.entity.AboutInfo;
import com.example.portfolio.repository.AboutInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Service
public class WhatsAppNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);

    private final AboutInfoRepository aboutInfoRepository;
    private final RestClient restClient;

    public WhatsAppNotificationService(AboutInfoRepository aboutInfoRepository) {
        this.aboutInfoRepository = aboutInfoRepository;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(4));
        requestFactory.setReadTimeout(Duration.ofSeconds(8));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @Async
    public void sendContactAlert(String fromName, String fromEmail, String subject, String message) {
        try {
            AboutInfo about = aboutInfoRepository.findAll().stream().findFirst().orElse(null);
            if (about == null || !Boolean.TRUE.equals(about.getWhatsappNotificationEnabled())) {
                return;
            }

            String customWebhook = about.getWhatsappNotificationWebhookUrl();
            String phone = about.getWhatsappNotificationPhone();
            String apiKey = about.getWhatsappNotificationApiKey();

            // 1. Custom Webhook Dispatch (e.g. Zapier, Make, n8n, Twilio, Meta Cloud API)
            if (customWebhook != null && !customWebhook.isBlank()) {
                sendWebhookNotification(customWebhook.trim(), fromName, fromEmail, subject, message);
                return;
            }

            // 2. CallMeBot WhatsApp API (Free direct developer gateway)
            if (phone != null && !phone.isBlank() && apiKey != null && !apiKey.isBlank()) {
                sendCallMeBotNotification(phone.trim(), apiKey.trim(), fromName, fromEmail, subject, message);
            }
        } catch (Exception e) {
            log.warn("Failed to dispatch WhatsApp contact notification: {}", e.getMessage());
        }
    }

    private void sendCallMeBotNotification(String rawPhone, String apiKey, String fromName, String fromEmail, String subject, String message) {
        try {
            // Clean phone: ensure numeric without '+' prefix for CallMeBot URL query
            String cleanPhone = rawPhone.replaceAll("[^0-9]", "");
            if (cleanPhone.isBlank()) {
                log.warn("Cannot send WhatsApp alert: phone number is invalid");
                return;
            }

            String text = "⚡ *New Portfolio Message!*\n\n"
                    + "👤 *From:* " + (fromName != null ? fromName : "Anonymous") + "\n"
                    + "📧 *Email:* " + (fromEmail != null ? fromEmail : "N/A") + "\n"
                    + "📌 *Subject:* " + (subject != null && !subject.isBlank() ? subject : "(No subject)") + "\n\n"
                    + "💬 *Message:*\n" + (message != null ? message : "");

            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "https://api.callmebot.com/whatsapp.php?phone=" + cleanPhone + "&text=" + encodedText + "&apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            log.info("WhatsApp notification sent via CallMeBot. Response: {}", response != null ? response.trim() : "OK");
        } catch (Exception ex) {
            log.warn("CallMeBot WhatsApp notification attempt failed: {}", ex.getMessage());
        }
    }

    private void sendWebhookNotification(String webhookUrl, String fromName, String fromEmail, String subject, String message) {
        try {
            Map<String, Object> payload = Map.of(
                    "event", "contact_form_submission",
                    "channel", "whatsapp",
                    "name", fromName != null ? fromName : "",
                    "email", fromEmail != null ? fromEmail : "",
                    "subject", subject != null ? subject : "",
                    "message", message != null ? message : ""
            );

            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("WhatsApp notification sent via custom webhook URL: {}", webhookUrl);
        } catch (Exception ex) {
            log.warn("WhatsApp custom webhook delivery failed: {}", ex.getMessage());
        }
    }
}
