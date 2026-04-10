package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByIncomeDateDesc(Long userId);

    List<Income> findByUserIdAndIncomeDateBetweenOrderByIncomeDateDesc(
            Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId " +
            "AND i.incomeDate BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndDateRange(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT FUNCTION('DATE_FORMAT', i.incomeDate, '%Y-%m') as month, SUM(i.amount) as total " +
            "FROM Income i WHERE i.user.id = :userId " +
            "GROUP BY month ORDER BY month DESC")
    List<Object[]> monthlyTotals(Long userId);
}
