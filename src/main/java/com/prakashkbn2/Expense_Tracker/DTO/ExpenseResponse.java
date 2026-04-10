package com.prakashkbn2.Expense_Tracker.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String category;
    private String notes;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
}
