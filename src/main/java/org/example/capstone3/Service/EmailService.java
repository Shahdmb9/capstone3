package org.example.capstone3.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Habit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendReportByEmail(String toEmail, byte[] pdfBytes, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(name + " Your  Children Performance Report ");
            helper.setText("your  Children Performance Report  attached.");
            helper.addAttachment(
                    " Children_Performance_Report " + name + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf"
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new ApiException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendParentNotification(Habit habit) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(habit.getParent().getEmail());
            helper.setSubject(habit.getParent().getFullName() + " Your need to approval  something ");
            helper.setText(String.format("""
                            Dear %s,
                            
                            Your child %s has marked the habit "%s" as completed and is awaiting your approval.
                            
                            Please log in to review and approve or reject this request.
                            
                            Best regards,
                            The App Team
                            """,
                    habit.getParent().getFullName(),
                    habit.getChild().getFullName(),
                    habit.getTitle()
            ));

            mailSender.send(message);
        } catch (Exception e) {
            throw new ApiException("Failed to send email: " + e.getMessage());
        }

    }


}

