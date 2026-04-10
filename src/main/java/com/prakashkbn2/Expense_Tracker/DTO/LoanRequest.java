package com.prakashkbn2.Expense_Tracker.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanRequest {
    @NotBlank
    private String lenderName;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmount;

    private BigDecimal interestRate;

    @NotNull
    private LocalDate loanDate;

    private LocalDate dueDate;

    private String notes;
}
