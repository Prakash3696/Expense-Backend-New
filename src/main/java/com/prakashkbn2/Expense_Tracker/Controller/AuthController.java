package com.prakashkbn2.Expense_Tracker.Controller;

import com.prakashkbn2.Expense_Tracker.DTO.*;
import com.prakashkbn2.Expense_Tracker.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Controller/AuthController.java
 * ACTION: REPLACE existing file
 *
 * Endpoints:
 *   POST /api/auth/register         – email-based registration
 *   POST /api/auth/login            – email-based login
 *   POST /api/auth/forgot-password  – send 6-digit OTP to email
 *   POST /api/auth/verify-otp       – verify OTP (marks it verified)
 *   POST /api/auth/reset-password   – set new password after OTP verified
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {
        authService.sendOtp(req);
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to " + req.getEmail() + ". Valid for 5 minutes."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest req) {
        authService.verifyOtp(req);
        return ResponseEntity.ok(Map.of("message", "OTP verified. You may now reset your password."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully. Please log in."));
    }
}