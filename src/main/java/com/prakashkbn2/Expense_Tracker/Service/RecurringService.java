package com.prakashkbn2.Expense_Tracker.Service;

import com.prakashkbn2.Expense_Tracker.DTO.*;
import com.prakashkbn2.Expense_Tracker.Entity.*;
import com.prakashkbn2.Expense_Tracker.Entity.PendingTransaction.*;
import com.prakashkbn2.Expense_Tracker.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FILE: src/main/java/com/prakashkbn2/Expense_Tracker/Service/RecurringService.java
 * ACTION: REPLACE existing file completely with this version
 *
 * Key changes:
 *  - triggerMonthly() renamed to triggerForDate() and handles DAILY/WEEKLY/MONTHLY
 *  - Pending transaction generation on trigger
 *  - CRUD for pending transactions
 */
@Service
public class RecurringService {

    @Autowired private RecurringExpenseRepository  recurringExpenseRepo;
    @Autowired private RecurringIncomeRepository   recurringIncomeRepo;
    @Autowired private ExpenseRepository           expenseRepository;
    @Autowired private IncomeRepository            incomeRepository;
    @Autowired private PendingTransactionRepository pendingRepo;
    @Autowired private UserRepository              userRepository;

    // ═══════════════════════════════════════════════════════════
    // RECURRING EXPENSES — CRUD
    // ═══════════════════════════════════════════════════════════

    public List<RecurringExpenseResponse> getAllRecurringExpenses(String username) {
        User user = getUser(username);
        return recurringExpenseRepo.findByUserId(user.getId())
                .stream().map(this::toExpenseResponse).collect(Collectors.toList());
    }

    @Transactional
    public RecurringExpenseResponse createRecurringExpense(String username, RecurringExpenseRequest req) {
        User user = getUser(username);
        validateFrequencyFields(req.getFrequency(), req.getDayOfMonth(), req.getDayOfWeek());

        RecurringExpense re = RecurringExpense.builder()
                .user(user)
                .amount(req.getAmount())
                .category(req.getCategory())
                .dayOfMonth(req.getDayOfMonth())
                .dayOfWeek(req.getDayOfWeek())
                .frequency(req.getFrequency() != null ? req.getFrequency() : Frequency.MONTHLY)
                .notes(req.getNotes())
                .build();
        return toExpenseResponse(recurringExpenseRepo.save(re));
    }

    @Transactional
    public RecurringExpenseResponse updateRecurringExpense(String username, Long id, RecurringExpenseRequest req) {
        RecurringExpense re = getOwnedRecurringExpense(username, id);
        validateFrequencyFields(req.getFrequency(), req.getDayOfMonth(), req.getDayOfWeek());
        re.setAmount(req.getAmount());
        re.setCategory(req.getCategory());
        re.setDayOfMonth(req.getDayOfMonth());
        re.setDayOfWeek(req.getDayOfWeek());
        re.setFrequency(req.getFrequency() != null ? req.getFrequency() : Frequency.MONTHLY);
        re.setNotes(req.getNotes());
        return toExpenseResponse(recurringExpenseRepo.save(re));
    }

    @Transactional
    public void deleteRecurringExpense(String username, Long id) {
        recurringExpenseRepo.delete(getOwnedRecurringExpense(username, id));
    }

    // ═══════════════════════════════════════════════════════════
    // RECURRING INCOME — CRUD
    // ═══════════════════════════════════════════════════════════

    public List<RecurringIncomeResponse> getAllRecurringIncomes(String username) {
        User user = getUser(username);
        return recurringIncomeRepo.findByUserId(user.getId())
                .stream().map(this::toIncomeResponse).collect(Collectors.toList());
    }

    @Transactional
    public RecurringIncomeResponse createRecurringIncome(String username, RecurringIncomeRequest req) {
        User user = getUser(username);
        validateFrequencyFields(req.getFrequency(), req.getDayOfMonth(), req.getDayOfWeek());

        RecurringIncome ri = RecurringIncome.builder()
                .user(user)
                .amount(req.getAmount())
                .source(req.getSource())
                .dayOfMonth(req.getDayOfMonth())
                .dayOfWeek(req.getDayOfWeek())
                .frequency(req.getFrequency() != null ? req.getFrequency() : Frequency.MONTHLY)
                .notes(req.getNotes())
                .build();
        return toIncomeResponse(recurringIncomeRepo.save(ri));
    }

    @Transactional
    public RecurringIncomeResponse updateRecurringIncome(String username, Long id, RecurringIncomeRequest req) {
        RecurringIncome ri = getOwnedRecurringIncome(username, id);
        validateFrequencyFields(req.getFrequency(), req.getDayOfMonth(), req.getDayOfWeek());
        ri.setAmount(req.getAmount());
        ri.setSource(req.getSource());
        ri.setDayOfMonth(req.getDayOfMonth());
        ri.setDayOfWeek(req.getDayOfWeek());
        ri.setFrequency(req.getFrequency() != null ? req.getFrequency() : Frequency.MONTHLY);
        ri.setNotes(req.getNotes());
        return toIncomeResponse(recurringIncomeRepo.save(ri));
    }

    @Transactional
    public void deleteRecurringIncome(String username, Long id) {
        recurringIncomeRepo.delete(getOwnedRecurringIncome(username, id));
    }

    // ═══════════════════════════════════════════════════════════
    // TRIGGER — DAILY / WEEKLY / MONTHLY
    // ═══════════════════════════════════════════════════════════

    /**
     * Called from the controller with a target date.
     * Finds all matching recurring rules and:
     *  1. Creates real Expense / Income records  (actual transactions)
     *  2. Also creates / resolves PendingTransaction records
     */
    @Transactional
    public String triggerForDate(String username, LocalDate targetDate) {
        User user = getUser(username);
        int addedExpenses = 0;
        int addedIncomes  = 0;

        // ── DAILY: fire every recurring rule with frequency=DAILY ──
        for (RecurringExpense re : recurringExpenseRepo.findByUserIdAndFrequency(user.getId(), Frequency.DAILY)) {
            createExpenseEntry(user, re, targetDate);
            resolvePending(user, re.getId(), null, targetDate);
            addedExpenses++;
        }
        for (RecurringIncome ri : recurringIncomeRepo.findByUserIdAndFrequency(user.getId(), Frequency.DAILY)) {
            createIncomeEntry(user, ri, targetDate);
            resolvePending(user, null, ri.getId(), targetDate);
            addedIncomes++;
        }

        // ── WEEKLY: fire rules whose dayOfWeek matches targetDate ──
        int dow = targetDate.getDayOfWeek().getValue(); // 1=Mon … 7=Sun
        for (RecurringExpense re : recurringExpenseRepo.findByUserIdAndDayOfWeekAndFrequency(
                user.getId(), dow, Frequency.WEEKLY)) {
            createExpenseEntry(user, re, targetDate);
            resolvePending(user, re.getId(), null, targetDate);
            addedExpenses++;
        }
        for (RecurringIncome ri : recurringIncomeRepo.findByUserIdAndDayOfWeekAndFrequency(
                user.getId(), dow, Frequency.WEEKLY)) {
            createIncomeEntry(user, ri, targetDate);
            resolvePending(user, null, ri.getId(), targetDate);
            addedIncomes++;
        }

        // ── MONTHLY: fire rules whose dayOfMonth matches targetDate ──
        int dom = targetDate.getDayOfMonth();
        for (RecurringExpense re : recurringExpenseRepo.findByUserIdAndDayOfMonthAndFrequency(
                user.getId(), dom, Frequency.MONTHLY)) {
            createExpenseEntry(user, re, targetDate);
            resolvePending(user, re.getId(), null, targetDate);
            addedExpenses++;
        }
        for (RecurringIncome ri : recurringIncomeRepo.findByUserIdAndDayOfMonthAndFrequency(
                user.getId(), dom, Frequency.MONTHLY)) {
            createIncomeEntry(user, ri, targetDate);
            resolvePending(user, null, ri.getId(), targetDate);
            addedIncomes++;
        }

        return String.format(
                "Triggered for %s: %d expense(s) and %d income(s) added",
                targetDate, addedExpenses, addedIncomes);
    }

    // ═══════════════════════════════════════════════════════════
    // PENDING TRANSACTIONS — CRUD
    // ═══════════════════════════════════════════════════════════

    public List<PendingTransactionResponse> getAllPending(String username, String statusStr, String typeStr) {
        User user = getUser(username);
        Long uid  = user.getId();

        List<PendingTransaction> list;
        if (statusStr != null && typeStr != null) {
            list = pendingRepo.findByUserIdAndTypeAndStatusOrderByDueDateAsc(
                    uid,
                    PendingTransaction.TransactionType.valueOf(typeStr.toUpperCase()),
                    PendingStatus.valueOf(statusStr.toUpperCase()));
        } else if (statusStr != null) {
            list = pendingRepo.findByUserIdAndStatusOrderByDueDateAsc(
                    uid, PendingStatus.valueOf(statusStr.toUpperCase()));
        } else {
            list = pendingRepo.findByUserIdOrderByDueDateAsc(uid);
        }
        return list.stream().map(this::toPendingResponse).collect(Collectors.toList());
    }

    @Transactional
    public PendingTransactionResponse createPending(String username, PendingTransactionRequest req) {
        User user = getUser(username);
        PendingTransaction pt = PendingTransaction.builder()
                .user(user)
                .type(req.getType())
                .amount(req.getAmount())
                .label(req.getLabel())
                .notes(req.getNotes())
                .dueDate(req.getDueDate())
                .frequency(req.getFrequency())
                .status(PendingStatus.PENDING)
                .build();
        return toPendingResponse(pendingRepo.save(pt));
    }

    /**
     * Mark a pending item as COMPLETED and immediately post
     * the real Expense or Income entry.
     */
    @Transactional
    public PendingTransactionResponse completePending(String username, Long id) {
        PendingTransaction pt = getOwnedPending(username, id);
        if (pt.getStatus() != PendingStatus.PENDING) {
            throw new RuntimeException("Transaction is already " + pt.getStatus());
        }
        User user = pt.getUser();

        if (pt.getType() == TransactionType.EXPENSE) {
            Expense exp = Expense.builder()
                    .user(user)
                    .amount(pt.getAmount())
                    .category(pt.getLabel())
                    .notes("[PENDING→DONE] " + (pt.getNotes() != null ? pt.getNotes() : ""))
                    .expenseDate(pt.getDueDate())
                    .build();
            expenseRepository.save(exp);
        } else {
            Income inc = Income.builder()
                    .user(user)
                    .amount(pt.getAmount())
                    .source(pt.getLabel())
                    .notes("[PENDING→DONE] " + (pt.getNotes() != null ? pt.getNotes() : ""))
                    .incomeDate(pt.getDueDate())
                    .build();
            incomeRepository.save(inc);
        }

        pt.setStatus(PendingStatus.COMPLETED);
        pt.setCompletedAt(LocalDateTime.now());
        return toPendingResponse(pendingRepo.save(pt));
    }

    /**
     * Mark a pending item as SKIPPED (won't be posted).
     */
    @Transactional
    public PendingTransactionResponse skipPending(String username, Long id) {
        PendingTransaction pt = getOwnedPending(username, id);
        if (pt.getStatus() != PendingStatus.PENDING) {
            throw new RuntimeException("Only PENDING items can be skipped");
        }
        pt.setStatus(PendingStatus.SKIPPED);
        return toPendingResponse(pendingRepo.save(pt));
    }

    @Transactional
    public void deletePending(String username, Long id) {
        pendingRepo.delete(getOwnedPending(username, id));
    }

    // ═══════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════

    /** Creates a real Expense entry from a recurring rule */
    private void createExpenseEntry(User user, RecurringExpense re, LocalDate date) {
        Expense exp = Expense.builder()
                .user(user)
                .amount(re.getAmount())
                .category(re.getCategory())
                .notes("[AUTO-" + re.getFrequency() + "] " + (re.getNotes() != null ? re.getNotes() : ""))
                .expenseDate(date)
                .build();
        expenseRepository.save(exp);
    }

    /** Creates a real Income entry from a recurring rule */
    private void createIncomeEntry(User user, RecurringIncome ri, LocalDate date) {
        Income inc = Income.builder()
                .user(user)
                .amount(ri.getAmount())
                .source(ri.getSource())
                .notes("[AUTO-" + ri.getFrequency() + "] " + (ri.getNotes() != null ? ri.getNotes() : ""))
                .incomeDate(date)
                .build();
        incomeRepository.save(inc);
    }

    /**
     * When a trigger fires, mark any PENDING items linked to the
     * same recurring rule and due on or before targetDate as COMPLETED.
     */
    private void resolvePending(User user, Long recurringExpenseId, Long recurringIncomeId, LocalDate targetDate) {
        pendingRepo.findDueByUserId(user.getId(), targetDate).forEach(pt -> {
            boolean matches = (recurringExpenseId != null && recurringExpenseId.equals(pt.getRecurringExpenseId()))
                    || (recurringIncomeId != null && recurringIncomeId.equals(pt.getRecurringIncomeId()));
            if (matches) {
                pt.setStatus(PendingStatus.COMPLETED);
                pt.setCompletedAt(LocalDateTime.now());
                pendingRepo.save(pt);
            }
        });
    }

    private void validateFrequencyFields(Frequency freq, Integer dom, Integer dow) {
        if (freq == null) return;
        if (freq == Frequency.MONTHLY && dom == null) {
            throw new RuntimeException("dayOfMonth is required for MONTHLY frequency");
        }
        if (freq == Frequency.WEEKLY && dow == null) {
            throw new RuntimeException("dayOfWeek is required for WEEKLY frequency");
        }
    }

    private RecurringExpense getOwnedRecurringExpense(String username, Long id) {
        User user = getUser(username);
        RecurringExpense re = recurringExpenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring expense not found"));
        if (!re.getUser().getId().equals(user.getId())) throw new RuntimeException("Access denied");
        return re;
    }

    private RecurringIncome getOwnedRecurringIncome(String username, Long id) {
        User user = getUser(username);
        RecurringIncome ri = recurringIncomeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring income not found"));
        if (!ri.getUser().getId().equals(user.getId())) throw new RuntimeException("Access denied");
        return ri;
    }

    private PendingTransaction getOwnedPending(String username, Long id) {
        User user = getUser(username);
        PendingTransaction pt = pendingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pending transaction not found"));
        if (!pt.getUser().getId().equals(user.getId())) throw new RuntimeException("Access denied");
        return pt;
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Response mappers ────────────────────────────────────────

    private RecurringExpenseResponse toExpenseResponse(RecurringExpense re) {
        RecurringExpenseResponse r = new RecurringExpenseResponse();
        r.setId(re.getId());
        r.setAmount(re.getAmount());
        r.setCategory(re.getCategory());
        r.setDayOfMonth(re.getDayOfMonth());
        r.setDayOfWeek(re.getDayOfWeek());
        r.setFrequency(re.getFrequency());
        r.setNotes(re.getNotes());
        r.setCreatedAt(re.getCreatedAt());
        return r;
    }

    private RecurringIncomeResponse toIncomeResponse(RecurringIncome ri) {
        RecurringIncomeResponse r = new RecurringIncomeResponse();
        r.setId(ri.getId());
        r.setAmount(ri.getAmount());
        r.setSource(ri.getSource());
        r.setDayOfMonth(ri.getDayOfMonth());
        r.setDayOfWeek(ri.getDayOfWeek());
        r.setFrequency(ri.getFrequency());
        r.setNotes(ri.getNotes());
        r.setCreatedAt(ri.getCreatedAt());
        return r;
    }

    private PendingTransactionResponse toPendingResponse(PendingTransaction pt) {
        PendingTransactionResponse r = new PendingTransactionResponse();
        r.setId(pt.getId());
        r.setType(pt.getType());
        r.setAmount(pt.getAmount());
        r.setLabel(pt.getLabel());
        r.setNotes(pt.getNotes());
        r.setDueDate(pt.getDueDate());
        r.setStatus(pt.getStatus());
        r.setFrequency(pt.getFrequency());
        r.setRecurringExpenseId(pt.getRecurringExpenseId());
        r.setRecurringIncomeId(pt.getRecurringIncomeId());
        r.setCreatedAt(pt.getCreatedAt());
        r.setCompletedAt(pt.getCompletedAt());
        r.setOverdue(pt.getStatus() == PendingStatus.PENDING
                && pt.getDueDate().isBefore(LocalDate.now()));
        return r;
    }
}