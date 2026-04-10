package com.prakashkbn2.Expense_Tracker.DTO;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.PendingStatus;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/DTO/PendingTransactionResponse.java
 * ACTION: CREATE THIS FILE (new)
 */
@Data
public class PendingTransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String label;
    private String notes;
    private LocalDate dueDate;
    private PendingStatus status;
    private Frequency frequency;
    private Long recurringExpenseId;
    private Long recurringIncomeId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private boolean overdue;               // computed: dueDate is before today and still PENDING
}