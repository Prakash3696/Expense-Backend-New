package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/RegisterRequest.java
 * ACTION: REPLACE existing file
 */
@Data
public class RegisterRequest {

    private String fullName;          // optional display name

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}