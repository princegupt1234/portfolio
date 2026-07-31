package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Sends a notification email when a new contact form message arrives.
 * Disabled by default (app.mail.enabled=false) since it needs real SMTP
 * credentials in application.properties / environment variables to work.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;

    @Value("${app.mail.notify-to}")
    private String notifyTo;

    @Value("${app.mail.reply-from:${spring.mail.username:}}")
    private String replyFrom;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifyNewMessage(String fromName, String fromEmail, String subject, String message) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(notifyTo);
            mail.setSubject("New portfolio contact: " + (subject == null || subject.isBlank() ? "No subject" : subject));
            mail.setText("From: " + fromName + " <" + fromEmail + ">\n\n" + message);
            if (fromEmail != null && !fromEmail.isBlank()) {
                mail.setReplyTo(fromEmail);
            }
            applyFrom(mail);
            mailSender.send(mail);
        } catch (Exception ignored) {
            // Never let a mail failure break the contact form submission.
        }
    }

    public boolean sendReply(String toEmail, String toName, String subject, String body, String originalMessage) {
        if (!mailEnabled) {
            log.warn("Mail sending is disabled. Reply email not sent to {}", toEmail);
            return false;
        }
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Reply email not sent because recipient address is missing");
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            String html = buildReplyHtml(toName, body, originalMessage);
            helper.setText(buildReplyPlainText(toName, body, originalMessage), html);
            applyFrom(helper);
            mailSender.send(message);
            return true;
        } catch (MessagingException ex) {
            log.error("Failed to compose reply email to {}", toEmail, ex);
            return false;
        } catch (Exception ex) {
            log.error("Failed to send reply email to {}", toEmail, ex);
            return false;
        }
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
