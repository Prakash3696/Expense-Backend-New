package com.prakashkbn2.Expense_Tracker.Service;


import com.prakashkbn2.Expense_Tracker.DTO.DashboardResponse;
import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.ExpenseRepository;
import com.prakashkbn2.Expense_Tracker.Repository.IncomeRepository;
import com.prakashkbn2.Expense_Tracker.Repository.LoanRepository;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private IncomeRepository incomeRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;

    public DashboardResponse getDashboard(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd   = now.withDayOfMonth(now.lengthOfMonth());

        DashboardResponse dash = new DashboardResponse();

        // ── Current month totals ────────────────────────────────
        BigDecimal monthIncome  = incomeRepository.sumByUserIdAndDateRange(userId, monthStart, monthEnd);
        BigDecimal monthExpense = expenseRepository.sumByUserIdAndDateRange(userId, monthStart, monthEnd);
        dash.setTotalIncomeThisMonth(monthIncome);
        dash.setTotalExpensesThisMonth(monthExpense);
        dash.setNetSavingsThisMonth(monthIncome.subtract(monthExpense));

        // ── Loan summary ────────────────────────────────────────
        dash.setTotalOutstandingLoans(loanRepository.totalOutstandingByUserId(userId));
        dash.setActiveLoanCount(
                loanRepository.findByUserIdAndStatus(userId, com.prakashkbn2.Expense_Tracker.Entity.Loan.LoanStatus.ACTIVE).size());

        // ── Last 6 months income ────────────────────────────────
        List<Object[]> incomeMonthly = incomeRepository.monthlyTotals(userId);
        dash.setLast6MonthsIncome(toMonthlyList(incomeMonthly, 6));

        // ── Last 6 months expenses ──────────────────────────────
        List<Object[]> expenseMonthly = expenseRepository.monthlyTotals(userId);
        dash.setLast6MonthsExpenses(toMonthlyList(expenseMonthly, 6));

        // ── Expenses by category (current month) ────────────────
        List<Object[]> catData = expenseRepository.sumByCategoryAndDateRange(userId, monthStart, monthEnd);
        List<DashboardResponse.CategoryData> categories = new ArrayList<>();
        for (Object[] row : catData) {
            categories.add(new DashboardResponse.CategoryData(
                    (String) row[0],
                    (BigDecimal) row[1]
            ));
        }
        dash.setExpensesByCategory(categories);

        return dash;
    }

    private List<DashboardResponse.MonthlyData> toMonthlyList(List<Object[]> rows, int limit) {
        List<DashboardResponse.MonthlyData> list = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count++ >= limit) break;
            list.add(new DashboardResponse.MonthlyData(
                    (String) row[0],
                    (BigDecimal) row[1]
            ));
        }
        // Reverse so oldest month is first (chronological order for charts)
        java.util.Collections.reverse(list);
        return list;
    }
}
