package com.prakashkbn2.Expense_Tracker.DTO;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.PendingStatus;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/DTO/PendingTransactionRequest.java
 * ACTION: CREATE THIS FILE (new)
 */
@Data
public class PendingTransactionRequest {

    @NotNull
    private TransactionType type;          // INCOME or EXPENSE

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String label;                  // category (expense) or source (income)

    private String notes;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private Frequency frequency;           // DAILY | WEEKLY | MONTHLY
}