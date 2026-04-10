package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByLoanDateDesc(Long userId);

    List<Loan> findByUserIdAndStatus(Long userId, Loan.LoanStatus status);

    @Query("SELECT COALESCE(SUM(l.remainingAmount), 0) FROM Loan l " +
            "WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    BigDecimal totalOutstandingByUserId(Long userId);
}
