package com.prakashkbn2.Expense_Tracker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Entity/RecurringIncome.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added 'frequency' and 'dayOfWeek' fields
 */
@Entity
@Table(name = "recurring_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String source;

    // Used when frequency = MONTHLY (1–31)
    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    // Used when frequency = WEEKLY (1=Monday … 7=Sunday)
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    // NEW: DAILY | WEEKLY | MONTHLY
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Frequency frequency = Frequency.MONTHLY;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}