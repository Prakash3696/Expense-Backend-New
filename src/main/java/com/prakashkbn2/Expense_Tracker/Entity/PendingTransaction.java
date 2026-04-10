package com.prakashkbn2.Expense_Tracker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Entity/PendingTransaction.java
 * ACTION: CREATE THIS FILE (new)
 *
 * Represents a transaction that is due but not yet confirmed/triggered.
 * When triggered it is converted into a real Expense or Income record.
 */
@Entity
@Table(name = "pending_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingTransaction {

    public enum TransactionType { INCOME, EXPENSE }
    public enum PendingStatus   { PENDING, COMPLETED, SKIPPED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // INCOME or EXPENSE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // category (for EXPENSE) or source (for INCOME) — stored in same column
    @Column(nullable = false, length = 100)
    private String label;

    @Column(length = 500)
    private String notes;

    // The date this transaction is due
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PendingStatus status = PendingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Frequency frequency;

    // Optional: link back to the recurring rule that created this pending item
    @Column(name = "recurring_expense_id")
    private Long recurringExpenseId;

    @Column(name = "recurring_income_id")
    private Long recurringIncomeId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}