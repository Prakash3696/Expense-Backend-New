package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/LoginRequest.java
 * ACTION: REPLACE existing file
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}