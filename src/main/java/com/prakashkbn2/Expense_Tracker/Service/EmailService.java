package com.prakashkbn2.Expense_Tracker.Service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${app.sender.email}")
    private String fromEmail;

    @Value("${app.sendgrid.api.key}")
    private String apiKey;

    public void sendOtpEmail(String toEmail, String otp, String fullName) {
        String subject = "FinTrack – Your Password Reset OTP";
        String htmlContent = buildOtpHtml(otp, fullName != null ? fullName : "User");

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (IOException ex) {
            throw new RuntimeException("Failed to send OTP email: " + ex.getMessage());
        }
    }

    private String buildOtpHtml(String otp, String name) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8"/>
                </head>
                <body style="font-family:Segoe UI; background:#f5f5f5; padding:20px;">
                  <div style="max-width:500px;margin:auto;background:#fff;border-radius:10px;padding:20px;">
                    <h2 style="color:#6c63ff;">FinTrack</h2>
                    <p>Hi <b>%s</b>,</p>
                    <p>Your OTP is:</p>
                    <h1 style="letter-spacing:8px;color:#6c63ff;">%s</h1>
                    <p>This OTP is valid for 5 minutes.</p>
                  </div>
                </body>
                </html>
                """.formatted(name, otp);
    }
}