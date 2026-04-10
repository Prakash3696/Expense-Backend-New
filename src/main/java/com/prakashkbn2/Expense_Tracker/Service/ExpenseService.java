package com.prakashkbn2.Expense_Tracker.Service;


import com.prakashkbn2.Expense_Tracker.DTO.ExpenseRequest;
import com.prakashkbn2.Expense_Tracker.DTO.ExpenseResponse;
import com.prakashkbn2.Expense_Tracker.Entity.Expense;
import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.ExpenseRepository;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private UserRepository userRepository;

    public List<ExpenseResponse> getAll(String username, LocalDate start, LocalDate end) {
        User user = getUser(username);
        List<Expense> expenses = (start != null && end != null)
                ? expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(user.getId(), start, end)
                : expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId());
        return expenses.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponse create(String username, ExpenseRequest req) {
        User user = getUser(username);
        Expense expense = Expense.builder()
                .user(user)
                .amount(req.getAmount())
                .category(req.getCategory())
                .notes(req.getNotes())
                .expenseDate(req.getExpenseDate())
                .build();
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(String username, Long id, ExpenseRequest req) {
        Expense expense = getOwnedExpense(username, id);
        expense.setAmount(req.getAmount());
        expense.setCategory(req.getCategory());
        expense.setNotes(req.getNotes());
        expense.setExpenseDate(req.getExpenseDate());
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(String username, Long id) {
        Expense expense = getOwnedExpense(username, id);
        expenseRepository.delete(expense);
    }

    // Internal use for recording loan disbursements and repayments
    @Transactional
    public Expense createInternal(User user, Expense expense) {
        return expenseRepository.save(expense);
    }

    private Expense getOwnedExpense(String username, Long id) {
        User user = getUser(username);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return expense;
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ExpenseResponse toResponse(Expense e) {
        ExpenseResponse r = new ExpenseResponse();
        r.setId(e.getId());
        r.setAmount(e.getAmount());
        r.setCategory(e.getCategory());
        r.setNotes(e.getNotes());
        r.setExpenseDate(e.getExpenseDate());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
