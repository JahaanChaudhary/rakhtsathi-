package com.app.blooddonor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ── Notify donor about a blood request ─────────────────
    @Async
    public void sendDonorNotification(String toEmail, String donorName,
                                       String bloodGroup, String city,
                                       String hospital, String message,
                                       String requesterPhone) {
        log.info("Sending donation request email to: {}", toEmail);
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("🩸 Urgent Blood Request — " + bloodGroup + " needed in " + city);
            mail.setText(
                "Dear " + donorName + ",\n\n" +
                "Someone urgently needs " + bloodGroup + " blood in " + city + ".\n\n" +
                "📍 Hospital : " + (hospital != null && !hospital.isEmpty() ? hospital : "Not specified") + "\n" +
                "📞 Contact  : " + requesterPhone + "\n" +
                "💬 Message  : " + (message != null && !message.isEmpty() ? message : "—") + "\n\n" +
                "Please log in to update your availability:\n" +
                "http://localhost:8080/donor/dashboard\n\n" +
                "Thank you for being a life saver! 🙏\n\n" +
                "— Blood Donor Finder Team"
            );
            mailSender.send(mail);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    // ── Welcome email after donor registration ─────────────
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        log.info("Sending welcome email to: {}", toEmail);
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("Welcome to Blood Donor Finder 🩸");
            mail.setText(
                "Hi " + name + ",\n\n" +
                "Welcome to Blood Donor Finder! Your profile is now active.\n\n" +
                "Manage your availability:\n" +
                "http://localhost:8080/donor/dashboard\n\n" +
                "Thank you for making a difference! 💙\n\n" +
                "— Blood Donor Finder Team"
            );
            mailSender.send(mail);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}
