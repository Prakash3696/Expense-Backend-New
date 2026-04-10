package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.Frequency;
import com.prakashkbn2.Expense_Tracker.Entity.RecurringIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Repository/RecurringIncomeRepository.java
 * ACTION: REPLACE existing file with this version
 * CHANGES: Added frequency-aware finder methods
 */
@Repository
public interface RecurringIncomeRepository extends JpaRepository<RecurringIncome, Long> {

    List<RecurringIncome> findByUserId(Long userId);

    // MONTHLY trigger — match day of month
    List<RecurringIncome> findByUserIdAndDayOfMonthAndFrequency(
            Long userId, Integer dayOfMonth, Frequency frequency);

    // WEEKLY trigger — match day of week
    List<RecurringIncome> findByUserIdAndDayOfWeekAndFrequency(
            Long userId, Integer dayOfWeek, Frequency frequency);

    // DAILY trigger — all daily rules for this user
    List<RecurringIncome> findByUserIdAndFrequency(Long userId, Frequency frequency);
}