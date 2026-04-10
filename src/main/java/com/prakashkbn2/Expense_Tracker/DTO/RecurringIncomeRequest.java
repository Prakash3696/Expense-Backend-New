package com.prakashkbn2.Expense_Tracker.DTO;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/DTO/RecurringIncomeRequest.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added frequency, dayOfWeek; made dayOfMonth optional
 */
@Data
public class RecurringIncomeRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String source;

    @Min(1) @Max(31)
    private Integer dayOfMonth;

    @Min(1) @Max(7)
    private Integer dayOfWeek;

    private Frequency frequency = Frequency.MONTHLY;

    private String notes;
}