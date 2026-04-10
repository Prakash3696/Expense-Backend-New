package com.prakashkbn2.Expense_Tracker.DTO;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/DTO/RecurringIncomeResponse.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added frequency, dayOfWeek fields
 */
@Data
public class RecurringIncomeResponse {
    private Long id;
    private BigDecimal amount;
    private String source;
    private Integer dayOfMonth;
    private Integer dayOfWeek;
    private Frequency frequency;
    private String notes;
    private LocalDateTime createdAt;
}