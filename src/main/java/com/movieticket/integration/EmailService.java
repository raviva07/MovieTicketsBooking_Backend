package com.movieticket.integration;

import com.movieticket.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlBody) {
        if (!StringUtils.hasText(to)) {
            throw new BadRequestException("Recipient email is required");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject != null ? subject : "");
            helper.setText(htmlBody != null ? htmlBody : "", true);

            mailSender.send(message);
            log.info("Email sent to {}", to);

        } catch (MessagingException ex) {
            log.error("Email sending failed", ex);
            throw new RuntimeException("Failed to send email", ex);
        }
    }

    public void sendEmailWithAttachment(String to,
                                        String subject,
                                        String htmlBody,
                                        byte[] attachment,
                                        String filename) {

        if (!StringUtils.hasText(to)) {
            throw new BadRequestException("Recipient email is required");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject != null ? subject : "");
            helper.setText(htmlBody != null ? htmlBody : "", true);

            if (attachment != null && attachment.length > 0) {
                helper.addAttachment(
                        filename != null ? filename : "attachment",
                        new ByteArrayResource(attachment)
                );
            }

            mailSender.send(message);
            log.info("Email with attachment sent to {}", to);

        } catch (MessagingException ex) {
            log.error("Email with attachment failed", ex);
            throw new RuntimeException("Failed to send email", ex);
        }
    }
}
