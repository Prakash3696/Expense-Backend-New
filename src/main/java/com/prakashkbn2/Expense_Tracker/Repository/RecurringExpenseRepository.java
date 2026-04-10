package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import com.prakashkbn2.Expense_Tracker.Entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Repository/RecurringExpenseRepository.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added frequency-aware finder methods
 */
@Repository
public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {

    List<RecurringExpense> findByUserId(Long userId);

    // MONTHLY trigger — match day of month
    List<RecurringExpense> findByUserIdAndDayOfMonthAndFrequency(
            Long userId, Integer dayOfMonth, Frequency frequency);

    // WEEKLY trigger — match day of week
    List<RecurringExpense> findByUserIdAndDayOfWeekAndFrequency(
            Long userId, Integer dayOfWeek, Frequency frequency);

    // DAILY trigger — all daily rules for this user
    List<RecurringExpense> findByUserIdAndFrequency(Long userId, Frequency frequency);
}