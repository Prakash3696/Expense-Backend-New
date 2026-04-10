package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.PendingStatus;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Repository/PendingTransactionRepository.java
 * ACTION: CREATE THIS FILE (new)
 */
@Repository
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction, Long> {

    // All pending items for a user (optionally filtered by status)
    List<PendingTransaction> findByUserIdOrderByDueDateAsc(Long userId);

    List<PendingTransaction> findByUserIdAndStatusOrderByDueDateAsc(Long userId, PendingStatus status);

    List<PendingTransaction> findByUserIdAndTypeAndStatusOrderByDueDateAsc(
            Long userId, TransactionType type, PendingStatus status);

    // Find items due on or before a date (used by trigger)
    @Query("SELECT p FROM PendingTransaction p " +
            "WHERE p.user.id = :userId AND p.status = 'PENDING' AND p.dueDate <= :date " +
            "ORDER BY p.dueDate ASC")
    List<PendingTransaction> findDueByUserId(Long userId, LocalDate date);

    // Count pending by type
    long countByUserIdAndTypeAndStatus(Long userId, TransactionType type, PendingStatus status);
}