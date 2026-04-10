package com.prakashkbn2.Expense_Tracker.DTO;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/DTO/RecurringExpenseRequest.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added frequency, dayOfWeek; made dayOfMonth optional
 */
@Data
public class RecurringExpenseRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String category;

    // Required only when frequency = MONTHLY
    @Min(1) @Max(31)
    private Integer dayOfMonth;

    // Required only when frequency = WEEKLY (1=Mon, 7=Sun)
    @Min(1) @Max(7)
    private Integer dayOfWeek;

    // DAILY | WEEKLY | MONTHLY  (default MONTHLY if omitted)
    private Frequency frequency = Frequency.MONTHLY;

    private String notes;
}