package com.prakashkbn2.Expense_Tracker.Service;


import com.prakashkbn2.Expense_Tracker.DTO.IncomeRequest;
import com.prakashkbn2.Expense_Tracker.DTO.IncomeResponse;
import com.prakashkbn2.Expense_Tracker.Entity.Income;
import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.IncomeRepository;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    @Autowired private IncomeRepository incomeRepository;
    @Autowired private UserRepository userRepository;

    public List<IncomeResponse> getAll(String username, LocalDate start, LocalDate end) {
        User user = getUser(username);
        List<Income> incomes = (start != null && end != null)
                ? incomeRepository.findByUserIdAndIncomeDateBetweenOrderByIncomeDateDesc(user.getId(), start, end)
                : incomeRepository.findByUserIdOrderByIncomeDateDesc(user.getId());
        return incomes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public IncomeResponse create(String username, IncomeRequest req) {
        User user = getUser(username);
        Income income = Income.builder()
                .user(user)
                .amount(req.getAmount())
                .source(req.getSource())
                .notes(req.getNotes())
                .incomeDate(req.getIncomeDate())
                .build();
        return toResponse(incomeRepository.save(income));
    }

    @Transactional
    public IncomeResponse update(String username, Long id, IncomeRequest req) {
        Income income = getOwned(username, id);
        income.setAmount(req.getAmount());
        income.setSource(req.getSource());
        income.setNotes(req.getNotes());
        income.setIncomeDate(req.getIncomeDate());
        return toResponse(incomeRepository.save(income));
    }

    @Transactional
    public void delete(String username, Long id) {
        incomeRepository.delete(getOwned(username, id));
    }

    private Income getOwned(String username, Long id) {
        User user = getUser(username);
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return income;
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public IncomeResponse toResponse(Income i) {
        IncomeResponse r = new IncomeResponse();
        r.setId(i.getId());
        r.setAmount(i.getAmount());
        r.setSource(i.getSource());
        r.setNotes(i.getNotes());
        r.setIncomeDate(i.getIncomeDate());
        r.setCreatedAt(i.getCreatedAt());
        return r;
    }
}
