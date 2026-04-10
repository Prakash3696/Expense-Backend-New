package com.prakashkbn2.Expense_Tracker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Entity/OtpToken.java
 * ACTION: CREATE (new file)
 *
 * Stores OTP codes for password-reset requests.
 * Each row is tied to an email. Expired rows are safe to delete.
 */
@Entity
@Table(name = "otp_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The email address this OTP was issued for */
    @Column(nullable = false, length = 150)
    private String email;

    /** 6-digit numeric OTP (stored as plain String; short-lived) */
    @Column(nullable = false, length = 10)
    private String otp;

    /** When this OTP stops being valid (createdAt + 5 minutes) */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Set to true once the OTP has been verified successfully */
    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}