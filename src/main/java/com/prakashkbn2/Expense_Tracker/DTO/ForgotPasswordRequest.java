package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/ForgotPasswordRequest.java
 * ACTION: CREATE (new file)
 */
@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;
}