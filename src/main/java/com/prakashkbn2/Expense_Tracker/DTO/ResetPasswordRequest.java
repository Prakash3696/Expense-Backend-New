package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/ResetPasswordRequest.java
 * ACTION: CREATE (new file)
 *
 * The frontend sends email + verified OTP + new password.
 * The backend re-checks the OTP is still marked verified before resetting.
 */
@Data
public class ResetPasswordRequest {

    @NotBlank @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otp;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}