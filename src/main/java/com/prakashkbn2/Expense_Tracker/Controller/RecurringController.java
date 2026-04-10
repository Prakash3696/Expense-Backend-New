package com.prakashkbn2.Expense_Tracker.Controller;

import com.prakashkbn2.Expense_Tracker.DTO.*;
import com.prakashkbn2.Expense_Tracker.Service.RecurringService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Controller/RecurringController.java
 * ACTION: REPLACE existing file completely with this version
 *
 * New endpoints added:
 *   POST   /api/recurring/trigger          (renamed param; now handles all frequencies)
 *   GET    /api/recurring/pending          (list pending transactions)
 *   POST   /api/recurring/pending          (create pending)
 *   POST   /api/recurring/pending/{id}/complete
 *   POST   /api/recurring/pending/{id}/skip
 *   DELETE /api/recurring/pending/{id}
 */
@RestController
@RequestMapping("/api/recurring")
public class RecurringController {

    @Autowired private RecurringService recurringService;

    // ── Recurring Expenses ──────────────────────────────────────

    @GetMapping("/expenses")
    public ResponseEntity<List<RecurringExpenseResponse>> getExpenses(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(recurringService.getAllRecurringExpenses(user.getUsername()));
    }

    @PostMapping("/expenses")
    public ResponseEntity<RecurringExpenseResponse> createExpense(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody RecurringExpenseRequest req) {
        return ResponseEntity.ok(recurringService.createRecurringExpense(user.getUsername(), req));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<RecurringExpenseResponse> updateExpense(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody RecurringExpenseRequest req) {
        return ResponseEntity.ok(recurringService.updateRecurringExpense(user.getUsername(), id, req));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        recurringService.deleteRecurringExpense(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Recurring Income ────────────────────────────────────────

    @GetMapping("/income")
    public ResponseEntity<List<RecurringIncomeResponse>> getIncome(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(recurringService.getAllRecurringIncomes(user.getUsername()));
    }

    @PostMapping("/income")
    public ResponseEntity<RecurringIncomeResponse> createIncome(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody RecurringIncomeRequest req) {
        return ResponseEntity.ok(recurringService.createRecurringIncome(user.getUsername(), req));
    }

    @PutMapping("/income/{id}")
    public ResponseEntity<RecurringIncomeResponse> updateIncome(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody RecurringIncomeRequest req) {
        return ResponseEntity.ok(recurringService.updateRecurringIncome(user.getUsername(), id, req));
    }

    @DeleteMapping("/income/{id}")
    public ResponseEntity<Void> deleteIncome(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        recurringService.deleteRecurringIncome(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Trigger (Daily / Weekly / Monthly) ─────────────────────

    /**
     * POST /api/recurring/trigger?date=YYYY-MM-DD
     *
     * Fires all recurring rules (across all frequencies) that match
     * the supplied date. The service resolves DAILY always, WEEKLY by
     * day-of-week, and MONTHLY by day-of-month.
     */
    @PostMapping("/trigger")
    public ResponseEntity<String> trigger(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(recurringService.triggerForDate(user.getUsername(), date));
    }

    // ── Pending Transactions ────────────────────────────────────

    /**
     * GET /api/recurring/pending
     * Optional query params:
     *   ?status=PENDING|COMPLETED|SKIPPED
     *   ?type=INCOME|EXPENSE
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PendingTransactionResponse>> getPending(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(recurringService.getAllPending(user.getUsername(), status, type));
    }

    @PostMapping("/pending")
    public ResponseEntity<PendingTransactionResponse> createPending(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PendingTransactionRequest req) {
        return ResponseEntity.ok(recurringService.createPending(user.getUsername(), req));
    }

    @PostMapping("/pending/{id}/complete")
    public ResponseEntity<PendingTransactionResponse> completePending(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(recurringService.completePending(user.getUsername(), id));
    }

    @PostMapping("/pending/{id}/skip")
    public ResponseEntity<PendingTransactionResponse> skipPending(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(recurringService.skipPending(user.getUsername(), id));
    }

    @DeleteMapping("/pending/{id}")
    public ResponseEntity<Void> deletePending(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        recurringService.deletePending(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}