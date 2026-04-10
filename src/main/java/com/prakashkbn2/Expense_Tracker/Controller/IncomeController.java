package com.prakashkbn2.Expense_Tracker.Controller;


import com.prakashkbn2.Expense_Tracker.DTO.IncomeRequest;
import com.prakashkbn2.Expense_Tracker.DTO.IncomeResponse;
import com.prakashkbn2.Expense_Tracker.Service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired private IncomeService incomeService;

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAll(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(incomeService.getAll(user.getUsername(), start, end));
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> create(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody IncomeRequest req) {
        return ResponseEntity.ok(incomeService.create(user.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> update(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest req) {
        return ResponseEntity.ok(incomeService.update(user.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        incomeService.delete(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
