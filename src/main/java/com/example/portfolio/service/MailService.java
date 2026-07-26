package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends a notification email when a new contact form message arrives.
 * Disabled by default (app.mail.enabled=false) since it needs real SMTP
 * credentials in application.properties / environment variables to work.
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;

    @Value("${app.mail.notify-to}")
    private String notifyTo;

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
            mailSender.send(mail);
        } catch (Exception ignored) {
            // Never let a mail failure break the contact form submission.
        }
    }
}
