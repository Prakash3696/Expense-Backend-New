package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/VerifyOtpRequest.java
 * ACTION: CREATE (new file)
 */
@Data
public class VerifyOtpRequest {

    @NotBlank @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "OTP must be exactly 6 digits")
    private String otp;
}