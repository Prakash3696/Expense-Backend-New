package com.prakashkbn2.Expense_Tracker.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Service/EmailService.java
 * ACTION: CREATE (new file)
 *
 * Wraps JavaMailSender to send styled HTML emails.
 * Configure SMTP credentials in application.properties.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends the OTP to the user's email in a clean HTML template.
     */
    public void sendOtpEmail(String toEmail, String otp, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("FinTrack – Your Password Reset OTP");
            helper.setText(buildOtpHtml(otp, fullName != null ? fullName : "User"), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    private String buildOtpHtml(String otp, String name) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; margin: 0; padding: 0; }
                    .container { max-width: 500px; margin: 40px auto; background: #fff;
                                 border-radius: 12px; overflow: hidden;
                                 box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
                    .header { background: #6c63ff; padding: 28px; text-align: center; }
                    .header h1 { color: #fff; margin: 0; font-size: 24px; letter-spacing: 1px; }
                    .body { padding: 32px; }
                    .body p { color: #444; line-height: 1.6; }
                    .otp-box { background: #f0efff; border: 2px dashed #6c63ff;
                               border-radius: 10px; text-align: center; padding: 20px;
                               margin: 24px 0; }
                    .otp-box span { font-size: 40px; font-weight: 700; color: #6c63ff;
                                    letter-spacing: 10px; }
                    .note { font-size: 13px; color: #888; margin-top: 16px; }
                    .footer { background: #fafafa; padding: 16px; text-align: center;
                              font-size: 12px; color: #aaa; border-top: 1px solid #eee; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header"><h1>💸 FinTrack</h1></div>
                    <div class="body">
                      <p>Hi <strong>%s</strong>,</p>
                      <p>We received a request to reset your FinTrack password.
                         Use the OTP below to proceed:</p>
                      <div class="otp-box"><span>%s</span></div>
                      <p class="note">⏱ This OTP is valid for <strong>5 minutes</strong>.
                         Do not share it with anyone.</p>
                      <p>If you did not request this, please ignore this email.</p>
                    </div>
                    <div class="footer">© FinTrack – Personal Finance Tracker</div>
                  </div>
                </body>
                </html>
                """.formatted(name, otp);
    }
}