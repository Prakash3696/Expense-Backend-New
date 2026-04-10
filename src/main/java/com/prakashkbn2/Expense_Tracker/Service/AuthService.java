package com.prakashkbn2.Expense_Tracker.Service;

import com.prakashkbn2.Expense_Tracker.DTO.*;
import com.prakashkbn2.Expense_Tracker.Entity.OtpToken;
import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.OtpTokenRepository;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import com.prakashkbn2.Expense_Tracker.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Service/AuthService.java
 * ACTION: REPLACE existing file completely
 *
 * Key changes:
 *  - register()  now uses email instead of username
 *  - login()     authenticates with email + password
 *  - sendOtp()   generates & emails a 6-digit OTP (TTL 5 min)
 *  - verifyOtp() checks correctness + expiry, marks verified
 *  - resetPassword() re-checks verified OTP, then resets + cleans up
 */
@Service
public class AuthService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired private UserRepository       userRepository;
    @Autowired private OtpTokenRepository   otpTokenRepository;
    @Autowired private PasswordEncoder      passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils             jwtUtils;
    @Autowired private EmailService         emailService;

    // ─────────────────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(email)
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        userRepository.save(user);

        String token = jwtUtils.generateTokenFromUsername(email);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
    }

    // ─────────────────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = jwtUtils.generateToken(auth);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
    }

    // ─────────────────────────────────────────────────────────
    //  FORGOT PASSWORD – SEND OTP
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void sendOtp(ForgotPasswordRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Purge old expired OTPs to keep the table clean
        otpTokenRepository.deleteAllExpired(LocalDateTime.now());

        // Generate cryptographically random 6-digit OTP
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .verified(false)
                .build();
        otpTokenRepository.save(token);

        // Send the OTP via email
        emailService.sendOtpEmail(email, otp, user.getFullName());
    }

    // ─────────────────────────────────────────────────────────
    //  VERIFY OTP
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void verifyOtp(VerifyOtpRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        OtpToken token = otpTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("No OTP found for this email. Please request a new one."));

        if (token.isExpired()) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }
        if (!token.getOtp().equals(req.getOtp())) {
            throw new RuntimeException("Incorrect OTP. Please check and try again.");
        }

        // Mark as verified — the reset-password step will check this flag
        token.setVerified(true);
        otpTokenRepository.save(token);
    }

    // ─────────────────────────────────────────────────────────
    //  RESET PASSWORD
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        OtpToken token = otpTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found. Please restart the password reset process."));

        if (token.isExpired()) {
            throw new RuntimeException("Session expired. Please request a new OTP.");
        }
        if (!token.isVerified()) {
            throw new RuntimeException("OTP not verified. Please verify your OTP first.");
        }
        if (!token.getOtp().equals(req.getOtp())) {
            throw new RuntimeException("OTP mismatch. Unauthorized request.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        // Clean up all OTPs for this email after successful reset
        otpTokenRepository.deleteAllByEmail(email);
    }
}