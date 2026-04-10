package com.prakashkbn2.Expense_Tracker.DTO;

import lombok.Data;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/DTO/AuthResponse.java
 * ACTION: REPLACE existing file
 * CHANGE: username field replaced with email
 */
@Data
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long   userId;
    private String email;
    private String fullName;

    public AuthResponse(String token, Long userId, String email, String fullName) {
        this.token    = token;
        this.userId   = userId;
        this.email    = email;
        this.fullName = fullName;
    }
}