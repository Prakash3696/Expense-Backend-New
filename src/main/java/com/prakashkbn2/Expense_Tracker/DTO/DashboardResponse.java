package com.prakashkbn2.Expense_Tracker.DTO;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    // Current month totals
    private BigDecimal totalIncomeThisMonth;
    private BigDecimal totalExpensesThisMonth;
    private BigDecimal netSavingsThisMonth;

    // All time
    private BigDecimal totalOutstandingLoans;
    private long activeLoanCount;

    // Chart data
    private List<MonthlyData> last6MonthsIncome;
    private List<MonthlyData> last6MonthsExpenses;
    private List<CategoryData> expensesByCategory;

    @Data
    public static class MonthlyData {
        private String month;
        private BigDecimal amount;

        public MonthlyData(String month, BigDecimal amount) {
            this.month = month;
            this.amount = amount;
        }
    }

    @Data
    public static class CategoryData {
        private String category;
        private BigDecimal amount;

        public CategoryData(String category, BigDecimal amount) {
            this.category = category;
            this.amount = amount;
        }
    }
}
