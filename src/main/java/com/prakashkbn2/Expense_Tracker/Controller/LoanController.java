package com.prakashkbn2.Expense_Tracker.Controller;


import com.prakashkbn2.Expense_Tracker.DTO.LoanRepaymentRequest;
import com.prakashkbn2.Expense_Tracker.DTO.LoanRequest;
import com.prakashkbn2.Expense_Tracker.DTO.LoanResponse;
import com.prakashkbn2.Expense_Tracker.Entity.Loan;
import com.prakashkbn2.Expense_Tracker.Service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired private LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAll(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Loan.LoanStatus status) {
        if (status != null) {
            return ResponseEntity.ok(loanService.getByStatus(user.getUsername(), status));
        }
        return ResponseEntity.ok(loanService.getAll(user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody LoanRequest req) {
        return ResponseEntity.ok(loanService.create(user.getUsername(), req));
    }

    @PostMapping("/{id}/repay")
    public ResponseEntity<LoanResponse> repay(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody LoanRepaymentRequest req) {
        return ResponseEntity.ok(loanService.repay(user.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        loanService.delete(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
