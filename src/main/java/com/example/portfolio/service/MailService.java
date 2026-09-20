package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final RestClient restClient;
    private final com.example.portfolio.repository.AboutInfoRepository aboutInfoRepository;

    // Resend HTTPS API (Port 443 - Recommended for Render)
    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${resend.from:Prince Gupt <onboarding@resend.dev>}")
    private String resendFrom;

    // Brevo HTTPS API (Port 443 - Alternative for Render)
    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:princegupt3052@gmail.com}")
    private String brevoSenderEmail;

    @Value("${brevo.sender.name:Prince Gupt}")
    private String brevoSenderName;

    // Traditional SMTP Configuration
    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.notify-to:}")
    private String notifyTo;

    @Value("${app.mail.reply-from:${spring.mail.username:}}")
    private String replyFrom;

    public MailService(@Autowired(required = false) JavaMailSender mailSender,
                       @Autowired(required = false) com.example.portfolio.repository.AboutInfoRepository aboutInfoRepository) {
        this.mailSender = mailSender;
        this.aboutInfoRepository = aboutInfoRepository;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(6));
        requestFactory.setReadTimeout(Duration.ofSeconds(12));
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    private com.example.portfolio.entity.AboutInfo getAboutInfo() {
        if (aboutInfoRepository == null) return null;
        try {
            return aboutInfoRepository.findAll().stream().findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public String getEffectiveResendApiKey() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getResendApiKey() != null && !about.getResendApiKey().isBlank()) {
            return about.getResendApiKey().trim();
        }
        return resendApiKey != null ? resendApiKey.trim() : "";
    }

    public String getEffectiveResendFrom() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getResendFrom() != null && !about.getResendFrom().isBlank()) {
            return about.getResendFrom().trim();
        }
        return resendFrom != null && !resendFrom.isBlank() ? resendFrom.trim() : "Prince Gupt <onboarding@resend.dev>";
    }

    public String getEffectiveBrevoApiKey() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getBrevoApiKey() != null && !about.getBrevoApiKey().isBlank()) {
            return about.getBrevoApiKey().trim();
        }
        return brevoApiKey != null ? brevoApiKey.trim() : "";
    }

    public String getEffectiveBrevoSenderEmail() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getBrevoSenderEmail() != null && !about.getBrevoSenderEmail().isBlank()) {
            return about.getBrevoSenderEmail().trim();
        }
        return brevoSenderEmail != null && !brevoSenderEmail.isBlank() ? brevoSenderEmail.trim() : "princegupt3052@gmail.com";
    }

    public String getEffectiveBrevoSenderName() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getBrevoSenderName() != null && !about.getBrevoSenderName().isBlank()) {
            return about.getBrevoSenderName().trim();
        }
        return brevoSenderName != null && !brevoSenderName.isBlank() ? brevoSenderName.trim() : "Prince Gupt";
    }

    public String getEffectiveNotifyTo() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getMailNotificationEmail() != null && !about.getMailNotificationEmail().isBlank()) {
            return about.getMailNotificationEmail().trim();
        }
        if (notifyTo != null && !notifyTo.isBlank()) {
            return notifyTo.trim();
        }
        if (about != null && about.getEmail() != null && !about.getEmail().isBlank()) {
            return about.getEmail().trim();
        }
        return (mailUsername != null && !mailUsername.isBlank()) ? mailUsername.trim() : "princegupt3052@gmail.com";
    }

    public String getEffectiveSendingMethod() {
        com.example.portfolio.entity.AboutInfo about = getAboutInfo();
        if (about != null && about.getMailSendingMethod() != null && !about.getMailSendingMethod().isBlank()) {
            return about.getMailSendingMethod().trim().toUpperCase();
        }
        return "AUTO";
    }

    public boolean isConfigured() {
        return isResendConfigured() || isBrevoConfigured() || isSmtpConfigured();
    }

    public boolean isResendConfigured() {
        return !getEffectiveResendApiKey().isBlank();
    }

    public boolean isBrevoConfigured() {
        return !getEffectiveBrevoApiKey().isBlank();
    }

    public boolean isSmtpConfigured() {
        return mailSender != null && mailEnabled && mailUsername != null && !mailUsername.isBlank();
    }

    @Async
    public void notifyNewMessage(String fromName, String fromEmail, String subject, String message) {
        if (!isConfigured()) {
            return;
        }
        String recipient = getEffectiveNotifyTo();

        String mailSubject = "New portfolio contact: " + (subject == null || subject.isBlank() ? "No subject" : subject);
        String textContent = "From: " + fromName + " <" + fromEmail + ">\n\n" + message;
        String htmlContent = "<div style='font-family:sans-serif; line-height:1.6; color:#1f2937; max-width:600px; padding:20px; border:1px solid #e5e7eb; border-radius:12px;'>"
                + "<h2 style='color:#2563eb; margin-top:0;'>New Portfolio Contact Message</h2>"
                + "<p><strong>From:</strong> " + toHtmlEscape(fromName) + " (&lt;" + toHtmlEscape(fromEmail) + "&gt;)</p>"
                + "<p><strong>Subject:</strong> " + toHtmlEscape(subject) + "</p>"
                + "<hr style='border:none; border-top:1px solid #e5e7eb; margin:16px 0;'/>"
                + "<p style='white-space:pre-wrap; background:#f9fafb; padding:12px; border-radius:8px; border:1px solid #e5e7eb;'>" + toHtmlEscape(message) + "</p>"
                + "<p style='font-size:12px; color:#6b7280; margin-bottom:0;'>Submitted from live portfolio.</p>"
                + "</div>";

        String method = getEffectiveSendingMethod();
        if ("RESEND".equals(method) && isResendConfigured()) {
            sendViaResend(recipient, "Portfolio Admin", mailSubject, htmlContent, textContent, fromEmail);
            return;
        } else if ("BREVO".equals(method) && isBrevoConfigured()) {
            sendViaBrevo(recipient, "Portfolio Admin", mailSubject, htmlContent, textContent, fromEmail);
            return;
        } else if ("SMTP".equals(method) && isSmtpConfigured()) {
            sendNotificationViaSmtp(recipient, mailSubject, textContent, fromEmail);
            return;
        }

        // AUTO: Resend -> Brevo -> SMTP
        if (isResendConfigured()) {
            MailResult res = sendViaResend(recipient, "Portfolio Admin", mailSubject, htmlContent, textContent, fromEmail);
            if (res == MailResult.SUCCESS) return;
            log.warn("Resend notification failed with {}. Trying Brevo fallback...", res);
        }
        if (isBrevoConfigured()) {
            MailResult res = sendViaBrevo(recipient, "Portfolio Admin", mailSubject, htmlContent, textContent, fromEmail);
            if (res == MailResult.SUCCESS) return;
            log.warn("Brevo notification failed with {}. Trying SMTP fallback...", res);
        }
        if (isSmtpConfigured()) {
            sendNotificationViaSmtp(recipient, mailSubject, textContent, fromEmail);
        }
    }

    private void sendNotificationViaSmtp(String recipient, String mailSubject, String textContent, String fromEmail) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(recipient);
            mail.setSubject(mailSubject);
            mail.setText(textContent);
            if (fromEmail != null && !fromEmail.isBlank()) {
                mail.setReplyTo(fromEmail);
            }
            applyFrom(mail);
            mailSender.send(mail);
        } catch (Exception e) {
            log.warn("Failed to deliver contact notification email via SMTP: {}", e.getMessage());
        }
    }

    public enum MailResult {
        SUCCESS,
        NOT_CONFIGURED,
        AUTH_ERROR,
        TIMEOUT,
        FAILED
    }

    public boolean sendReply(String toEmail, String toName, String subject, String body, String originalMessage) {
        return sendReplyWithResult(toEmail, toName, subject, body, originalMessage) == MailResult.SUCCESS;
    }

    public MailResult sendReplyWithResult(String toEmail, String toName, String subject, String body, String originalMessage) {
        if (!isConfigured()) {
            log.warn("Mail sending is disabled or not configured. Set RESEND_API_KEY, BREVO_API_KEY, or SMTP credentials in admin panel.");
            return MailResult.NOT_CONFIGURED;
        }
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Reply email not sent because recipient address is missing");
            return MailResult.FAILED;
        }

        String html = buildReplyHtml(toName, body, originalMessage);
        String plain = buildReplyPlainText(toName, body, originalMessage);
        String replyToAddress = (replyFrom != null && !replyFrom.isBlank()) ? replyFrom : mailUsername;
        if (replyToAddress == null || replyToAddress.isBlank()) {
            replyToAddress = getEffectiveNotifyTo();
        }

        String method = getEffectiveSendingMethod();
        if ("RESEND".equals(method)) {
            return isResendConfigured() ? sendViaResend(toEmail, toName, subject, html, plain, replyToAddress) : MailResult.NOT_CONFIGURED;
        } else if ("BREVO".equals(method)) {
            return isBrevoConfigured() ? sendViaBrevo(toEmail, toName, subject, html, plain, replyToAddress) : MailResult.NOT_CONFIGURED;
        } else if ("SMTP".equals(method)) {
            return isSmtpConfigured() ? sendReplyViaSmtp(toEmail, toName, subject, html, plain) : MailResult.NOT_CONFIGURED;
        }

        // AUTO: Resend (HTTPS 443) -> Brevo (HTTPS 443) -> SMTP
        MailResult lastResult = MailResult.NOT_CONFIGURED;
        if (isResendConfigured()) {
            lastResult = sendViaResend(toEmail, toName, subject, html, plain, replyToAddress);
            if (lastResult == MailResult.SUCCESS) {
                return lastResult;
            }
            log.warn("Resend attempt failed with result {}. Falling back to next method...", lastResult);
        }
        if (isBrevoConfigured()) {
            MailResult brevoRes = sendViaBrevo(toEmail, toName, subject, html, plain, replyToAddress);
            if (brevoRes == MailResult.SUCCESS) {
                return brevoRes;
            }
            lastResult = brevoRes;
            log.warn("Brevo attempt failed with result {}. Falling back to SMTP...", lastResult);
        }
        if (isSmtpConfigured()) {
            MailResult smtpRes = sendReplyViaSmtp(toEmail, toName, subject, html, plain);
            if (smtpRes == MailResult.SUCCESS) {
                return smtpRes;
            }
            lastResult = smtpRes;
        }
        return lastResult;
    }

    private MailResult sendViaResend(String toEmail, String toName, String subject, String htmlContent, String textContent, String replyToAddress) {
        try {
            String apiKey = getEffectiveResendApiKey();
            String from = getEffectiveResendFrom();
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", List.of(toEmail.trim()));
            payload.put("subject", subject);
            payload.put("html", htmlContent);
            payload.put("text", textContent);
            if (replyToAddress != null && !replyToAddress.isBlank()) {
                payload.put("reply_to", replyToAddress.trim());
            }

            ResponseEntity<String> response = restClient.post()
                    .uri("https://api.resend.com/emails")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email delivered successfully via Resend HTTPS API to {}", toEmail);
                return MailResult.SUCCESS;
            } else {
                log.error("Resend API returned non-2xx status: {}", response.getStatusCode());
                return MailResult.FAILED;
            }
        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Resend API authentication failed. Verify RESEND_API_KEY in admin panel: {}", ex.getMessage());
            return MailResult.AUTH_ERROR;
        } catch (HttpClientErrorException.Forbidden ex) {
            log.error("Resend API forbidden: {}", ex.getResponseBodyAsString());
            return MailResult.FAILED;
        } catch (ResourceAccessException ex) {
            log.error("Resend API network timeout: {}", ex.getMessage());
            return MailResult.TIMEOUT;
        } catch (Exception ex) {
            log.error("Failed to send email via Resend API to {}: {}", toEmail, ex.getMessage(), ex);
            return MailResult.FAILED;
        }
    }

    private MailResult sendViaBrevo(String toEmail, String toName, String subject, String htmlContent, String textContent, String replyToAddress) {
        try {
            String apiKey = getEffectiveBrevoApiKey();
            String senderName = getEffectiveBrevoSenderName();
            String senderEmail = getEffectiveBrevoSenderEmail();
            Map<String, Object> sender = Map.of(
                    "name", senderName,
                    "email", senderEmail
            );
            Map<String, Object> recipient = Map.of(
                    "email", toEmail.trim(),
                    "name", toName != null ? toName.trim() : ""
            );
            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender);
            payload.put("to", List.of(recipient));
            payload.put("subject", subject);
            payload.put("htmlContent", htmlContent);
            payload.put("textContent", textContent);
            if (replyToAddress != null && !replyToAddress.isBlank()) {
                payload.put("replyTo", Map.of("email", replyToAddress.trim()));
            }

            ResponseEntity<String> response = restClient.post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .header("api-key", apiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email delivered successfully via Brevo HTTPS API to {}", toEmail);
                return MailResult.SUCCESS;
            } else {
                log.error("Brevo API returned non-2xx status: {}", response.getStatusCode());
                return MailResult.FAILED;
            }
        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Brevo API authentication failed. Verify BREVO_API_KEY: {}", ex.getMessage());
            return MailResult.AUTH_ERROR;
        } catch (ResourceAccessException ex) {
            log.error("Brevo API network timeout: {}", ex.getMessage());
            return MailResult.TIMEOUT;
        } catch (Exception ex) {
            log.error("Failed to send email via Brevo API to {}: {}", toEmail, ex.getMessage(), ex);
            return MailResult.FAILED;
        }
    }

    private MailResult sendReplyViaSmtp(String toEmail, String toName, String subject, String html, String plain) {
        if (!isSmtpConfigured()) {
            return MailResult.NOT_CONFIGURED;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(plain, html);
            applyFrom(helper);
            mailSender.send(message);
            return MailResult.SUCCESS;
        } catch (jakarta.mail.AuthenticationFailedException ex) {
            log.error("SMTP Authentication failed when replying to {}. Verify MAIL_PASSWORD/App Password.", toEmail, ex);
            return MailResult.AUTH_ERROR;
        } catch (MessagingException ex) {
            log.error("Failed to compose/send reply email via SMTP to {}", toEmail, ex);
            if (isTimeout(ex)) return MailResult.TIMEOUT;
            if (isAuth(ex)) return MailResult.AUTH_ERROR;
            return MailResult.FAILED;
        } catch (Exception ex) {
            log.error("Failed to send reply email via SMTP to {}", toEmail, ex);
            if (isTimeout(ex)) return MailResult.TIMEOUT;
            if (isAuth(ex)) return MailResult.AUTH_ERROR;
            return MailResult.FAILED;
        }
    }

    private boolean isTimeout(Throwable t) {
        Throwable curr = t;
        while (curr != null) {
            if (curr instanceof java.net.SocketTimeoutException) {
                return true;
            }
            if (curr.getMessage() != null && curr.getMessage().toLowerCase().contains("timeout")) {
                return true;
            }
            curr = curr.getCause();
        }
        return false;
    }

    private boolean isAuth(Throwable t) {
        Throwable curr = t;
        while (curr != null) {
            if (curr instanceof jakarta.mail.AuthenticationFailedException || (curr.getMessage() != null && curr.getMessage().toLowerCase().contains("authentication"))) {
                return true;
            }
            curr = curr.getCause();
        }
        return false;
    }

    private String buildReplyHtml(String recipientName, String replyBody, String originalMessage) {
        String greeting = recipientName != null && !recipientName.isBlank() ? recipientName : "there";
        return "<html>" +
               "<head>" +
               "<meta charset='UTF-8' />" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
               "<style>" +
               "body { font-family: 'Inter', system-ui, sans-serif; background:#f4f7fb; color:#111827; margin:0; padding:0; }" +
               " .wrapper { width:100%; padding:24px 0; background:#f4f7fb; }" +
               " .container { width:100%; max-width:680px; margin:0 auto; background:#ffffff; border-radius:24px; overflow:hidden; box-shadow:0 18px 40px rgba(15,23,42,0.12); }" +
               " .hero { background:linear-gradient(135deg,#2563eb,#7c3aed); color:#fff; padding:40px 32px; text-align:center; }" +
               " .hero h1 { margin:0; font-size:32px; line-height:1.1; }" +
               " .hero p { margin:14px auto 0; max-width:520px; color:rgba(255,255,255,0.9); font-size:16px; }" +
               " .content { padding:32px; }" +
               " .greeting { font-size:18px; margin:0 0 20px; color:#111827; }" +
               " .message { font-size:16px; line-height:1.78; color:#4b5563; margin:0 0 24px; }" +
               " .section { margin-bottom:24px; padding:20px; border-radius:18px; background:#f8fafc; border:1px solid #e5e7eb; }" +
               " .section h2 { margin:0 0 12px; font-size:18px; color:#111827; }" +
               " .section p { margin:0; font-size:15px; line-height:1.75; color:#475569; }" +
               " .footer { padding:24px 32px 32px; color:#475569; font-size:14px; }" +
               " .footer strong { color:#111827; }" +
               " .social { margin:24px 18px 0 0; display:flex; gap:12px; flex-wrap:wrap; }" +
               " .social a { display:inline-flex; align-items:center; gap:8px; padding:10px 16px; border-radius:12px; border:1px solid #d1d5db; background:#f8fafc; color:#2563eb; text-decoration:none; font-weight:600; }" +
               " .social a:hover { text-decoration:underline; }" +
               " .social .icon { font-size:18px; line-height:1; }" +
               " .button { display:inline-flex; align-items:center; justify-content:center; padding:14px 28px; border-radius:12px; background:#2563eb; color:#fff; text-decoration:none; font-weight:600; margin-top:16px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='wrapper'>" +
               "<div class='container'>" +
               "<div class='hero'>" +
               "<h1>Your message is acknowledged</h1>" +
               "<p>Thank you for reaching out through my portfolio. Here is my reply to your inquiry.</p>" +
               "</div>" +
               "<div class='content'>" +
               "<p class='greeting'>Hi <strong>" + toHtmlEscape(greeting) + "</strong>,</p>" +
               "<div class='message'>" + toHtmlEscape(replyBody) + "</div>" +
               "<div class='section'>" +
               "<h2>Your original message</h2>" +
               "<p>" + (originalMessage == null || originalMessage.isBlank() ? "No original message available." : toHtmlEscape(originalMessage)) + "</p>" +
               "</div>" +
               "<div class='footer'>" +
               "<p>Best Regards,<br /><strong>Prince Gupta</strong><br />Full Stack Developer<br />Java • Spring Boot • React • Next.js</p>" +
               "<div class='social'>" +
               "<a href='https://github.com/princegupt1234' target='_blank'><span class='icon'>🐙</span>GitHub</a>" +
               "<a href='https://linkedin.com/in/prince-gupt-175289322' target='_blank'><span class='icon'>🔗</span>LinkedIn</a>" +
               "</div>" +
               "<p style='margin-top:16px; font-size:13px; color:#6b7280;'>Email: princegupt3052@gmail.com</p>" +
               "</div>" +
               "</div>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    private String buildReplyPlainText(String recipientName, String replyBody, String originalMessage) {
        String greeting = recipientName != null && !recipientName.isBlank() ? recipientName : "there";
        StringBuilder plain = new StringBuilder();
        plain.append("Hi ").append(greeting).append(",\n\n");
        plain.append(replyBody).append("\n\n");
        plain.append("Your original message:\n");
        plain.append(originalMessage != null ? originalMessage : "No original message available.");
        plain.append("\n\nBest Regards,\nPrince Gupta\nFull Stack Developer\nJava • Spring Boot • React • Next.js\nEmail: princegupt3052@gmail.com");
        return plain.toString();
    }

    private void applyFrom(MimeMessageHelper helper) throws MessagingException {
        if (replyFrom != null && !replyFrom.isBlank()) {
            helper.setFrom(replyFrom);
        }
    }

    private void applyFrom(SimpleMailMessage mail) {
        if (replyFrom != null && !replyFrom.isBlank()) {
            mail.setFrom(replyFrom);
        }
    }

    private String toHtmlEscape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("\n", "<br />");
    }
}
