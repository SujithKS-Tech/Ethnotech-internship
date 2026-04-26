package com.expensetracker.web;

import com.expensetracker.model.DashboardResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("message", "ok");
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@RequestParam(required = false) String month) {
        return expenseService.getDashboard(month);
    }

    @GetMapping("/expenses")
    public Map<String, List<Expense>> expenses(
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "All") String category
    ) {
        return Map.of("items", expenseService.getExpenses(month, category));
    }

    @PostMapping(value = "/expenses", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> createExpense(@RequestBody MultiValueMap<String, String> form) {
        Expense expense = expenseService.createExpense(toExpenseForm(form));
        return ResponseEntity.status(201).body(Map.of(
                "message", "Expense added.",
                "item", expense
        ));
    }

    @PutMapping(value = "/expenses/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> updateExpense(
            @PathVariable long id,
            @RequestBody MultiValueMap<String, String> form
    ) {
        Expense expense = expenseService.updateExpense(id, toExpenseForm(form));
        return Map.of("message", "Expense updated.", "item", expense);
    }

    @DeleteMapping("/expenses/{id}")
    public Map<String, String> deleteExpense(@PathVariable long id) {
        expenseService.deleteExpense(id);
        return Map.of("message", "Expense deleted.");
    }

    @PostMapping(value = "/budget", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> saveBudget(@RequestBody MultiValueMap<String, String> form) {
        expenseService.saveBudget(new BudgetForm(
                form.getFirst("month"),
                form.getFirst("budget")
        ));
        return Map.of("message", "Budget saved.");
    }

    private ExpenseForm toExpenseForm(MultiValueMap<String, String> form) {
        return new ExpenseForm(
                form.getFirst("title"),
                form.getFirst("category"),
                form.getFirst("amount"),
                form.getFirst("expenseDate"),
                form.getFirst("paymentMethod"),
                form.getFirst("notes")
        );
    }
}
