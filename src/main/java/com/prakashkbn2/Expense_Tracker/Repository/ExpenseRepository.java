package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
            Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId " +
            "AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndDateRange(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT e.category as category, SUM(e.amount) as total FROM Expense e " +
            "WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end " +
            "GROUP BY e.category ORDER BY total DESC")
    List<Object[]> sumByCategoryAndDateRange(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT FUNCTION('DATE_FORMAT', e.expenseDate, '%Y-%m') as month, SUM(e.amount) as total " +
            "FROM Expense e WHERE e.user.id = :userId " +
            "GROUP BY month ORDER BY month DESC")
    List<Object[]> monthlyTotals(Long userId);
}
