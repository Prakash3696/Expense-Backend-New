package com.prakashkbn2.Expense_Tracker.DTO;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IncomeResponse {
    private Long id;
    private BigDecimal amount;
    private String source;
    private String notes;
    private LocalDate incomeDate;
    private LocalDateTime createdAt;
}
