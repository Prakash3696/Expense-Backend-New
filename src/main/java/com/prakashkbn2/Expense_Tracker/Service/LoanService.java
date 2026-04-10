package com.prakashkbn2.Expense_Tracker.Service;


import com.prakashkbn2.Expense_Tracker.DTO.LoanRepaymentRequest;
import com.prakashkbn2.Expense_Tracker.DTO.LoanRequest;
import com.prakashkbn2.Expense_Tracker.DTO.LoanResponse;
import com.prakashkbn2.Expense_Tracker.Entity.Expense;
import com.prakashkbn2.Expense_Tracker.Entity.Loan;
import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.ExpenseRepository;
import com.prakashkbn2.Expense_Tracker.Repository.LoanRepository;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ExpenseRepository expenseRepository;

    public List<LoanResponse> getAll(String username) {
        User user = getUser(username);
        return loanRepository.findByUserIdOrderByLoanDateDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<LoanResponse> getByStatus(String username, Loan.LoanStatus status) {
        User user = getUser(username);
        return loanRepository.findByUserIdAndStatus(user.getId(), status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Creates a new loan and automatically records the borrowed amount as an expense
     * so it appears in the user's expense history.
     */
    @Transactional
    public LoanResponse create(String username, LoanRequest req) {
        User user = getUser(username);

        Loan loan = Loan.builder()
                .user(user)
                .lenderName(req.getLenderName())
                .totalAmount(req.getTotalAmount())
                .remainingAmount(req.getTotalAmount())
                .interestRate(req.getInterestRate())
                .loanDate(req.getLoanDate())
                .dueDate(req.getDueDate())
                .notes(req.getNotes())
                .status(Loan.LoanStatus.ACTIVE)
                .build();

        loan = loanRepository.save(loan);

        // Auto-record loan disbursement as an expense
        Expense disbursementExpense = Expense.builder()
                .user(user)
                .amount(req.getTotalAmount())
                .category("Loan Received")
                .notes("[LOAN] Borrowed from " + req.getLenderName()
                        + (req.getNotes() != null ? " — " + req.getNotes() : ""))
                .expenseDate(req.getLoanDate())
                .build();
        expenseRepository.save(disbursementExpense);

        return toResponse(loan);
    }

    /**
     * Records a repayment against a loan:
     * - Decreases the remaining amount
     * - Marks as CLEARED if fully repaid
     * - Records repayment as an expense entry
     */
    @Transactional
    public LoanResponse repay(String username, Long loanId, LoanRepaymentRequest req) {
        User user = getUser(username);
        Loan loan = getOwned(username, loanId);

        if (loan.getStatus() == Loan.LoanStatus.CLEARED) {
            throw new RuntimeException("Loan is already cleared");
        }

        BigDecimal repayAmount = req.getAmount();
        if (repayAmount.compareTo(loan.getRemainingAmount()) > 0) {
            throw new RuntimeException("Repayment amount (" + repayAmount
                    + ") exceeds remaining balance (" + loan.getRemainingAmount() + ")");
        }

        BigDecimal newRemaining = loan.getRemainingAmount().subtract(repayAmount);
        loan.setRemainingAmount(newRemaining);

        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.CLEARED);
        }

        loanRepository.save(loan);

        // Record repayment as an expense
        Expense repaymentExpense = Expense.builder()
                .user(user)
                .amount(repayAmount)
                .category("Loan Repayment")
                .notes("[REPAYMENT] To " + loan.getLenderName()
                        + (req.getNotes() != null ? " — " + req.getNotes() : ""))
                .expenseDate(req.getRepaymentDate())
                .build();
        expenseRepository.save(repaymentExpense);

        return toResponse(loan);
    }

    @Transactional
    public void delete(String username, Long id) {
        loanRepository.delete(getOwned(username, id));
    }

    private Loan getOwned(String username, Long id) {
        User user = getUser(username);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return loan;
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public LoanResponse toResponse(Loan l) {
        LoanResponse r = new LoanResponse();
        r.setId(l.getId());
        r.setLenderName(l.getLenderName());
        r.setTotalAmount(l.getTotalAmount());
        r.setRemainingAmount(l.getRemainingAmount());
        r.setPaidAmount(l.getTotalAmount().subtract(l.getRemainingAmount()));
        r.setInterestRate(l.getInterestRate());
        r.setLoanDate(l.getLoanDate());
        r.setDueDate(l.getDueDate());
        r.setStatus(l.getStatus());
        r.setNotes(l.getNotes());
        r.setCreatedAt(l.getCreatedAt());

        // Calculate repayment percentage
        if (l.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal paid = l.getTotalAmount().subtract(l.getRemainingAmount());
            double pct = paid.divide(l.getTotalAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            r.setRepaymentPercentage(pct);
        }
        return r;
    }
}
