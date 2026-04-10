package com.prakashkbn2.Expense_Tracker.DTO;


import com.prakashkbn2.Expense_Tracker.Entity.Loan;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoanResponse {
    private Long id;
    private String lenderName;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestRate;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private Loan.LoanStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private double repaymentPercentage;
}
