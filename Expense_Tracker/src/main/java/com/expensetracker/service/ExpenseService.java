package com.expensetracker.service;

import com.expensetracker.config.AppProperties;
import com.expensetracker.exception.ApiException;
import com.expensetracker.model.DashboardData;
import com.expensetracker.model.DashboardResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.web.BudgetForm;
import com.expensetracker.web.ExpenseForm;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private final ExpenseRepository repository;
    private final AppProperties appProperties;

    public ExpenseService(ExpenseRepository repository, AppProperties appProperties) {
        this.repository = repository;
        this.appProperties = appProperties;
    }

    public DashboardResponse getDashboard(String monthValue) {
        YearMonth month = parseMonth(monthValue);
        DashboardData data = repository.loadDashboard(month);
        return new DashboardResponse(
                month.toString(),
                appProperties.getCurrency(),
                data.summary(),
                data.categoryBreakdown(),
                data.recentExpenses()
        );
    }

    public List<Expense> getExpenses(String monthValue, String category) {
        YearMonth month = parseMonth(monthValue);
        String filter = category == null ? "All" : category;
        return repository.findExpenses(month, filter);
    }

    public Expense createExpense(ExpenseForm form) {
        return repository.createExpense(
                requiredText(form.title(), "Title", 120),
                requiredText(form.category(), "Category", 50),
                positiveAmount(form.amount()),
                requiredDate(form.expenseDate()),
                requiredText(form.paymentMethod(), "Payment method", 40),
                optionalText(form.notes(), 255)
        );
    }

    public Expense updateExpense(long id, ExpenseForm form) {
        boolean updated = repository.updateExpense(
                id,
                requiredText(form.title(), "Title", 120),
                requiredText(form.category(), "Category", 50),
                positiveAmount(form.amount()),
                requiredDate(form.expenseDate()),
                requiredText(form.paymentMethod(), "Payment method", 40),
                optionalText(form.notes(), 255)
        );

        if (!updated) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Expense not found.");
        }

        return repository.findExpenseById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found."));
    }

    public void deleteExpense(long id) {
        boolean deleted = repository.deleteExpense(id);
        if (!deleted) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Expense not found.");
        }
    }

    public void saveBudget(BudgetForm form) {
        YearMonth month = parseMonth(form.month());
        BigDecimal budget = positiveOrZeroAmount(form.budget());
        repository.upsertMonthlyBudget(month, budget);
    }

    private YearMonth parseMonth(String value) {
        if (value == null || value.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Month must be in YYYY-MM format.");
        }
    }

    private LocalDate requiredDate(String value) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expense date is required.");
        }

        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expense date must be in YYYY-MM-DD format.");
        }
    }

    private BigDecimal positiveAmount(String value) {
        BigDecimal amount = parseAmount(value, "Amount is required.");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero.");
        }
        return amount;
    }

    private BigDecimal positiveOrZeroAmount(String value) {
        BigDecimal amount = parseAmount(value, "Budget is required.");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Budget cannot be negative.");
        }
        return amount;
    }

    private BigDecimal parseAmount(String value, String emptyMessage) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, emptyMessage);
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be a valid number.");
        }
    }

    private String requiredText(String value, String label, int maxLength) {
        String sanitized = optionalText(value, maxLength);
        if (sanitized.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, label + " is required.");
        }
        return sanitized;
    }

    private String optionalText(String value, int maxLength) {
        String sanitized = value == null ? "" : value.trim();
        if (sanitized.length() > maxLength) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Value is too long. Maximum length is " + maxLength + " characters."
            );
        }
        return sanitized;
    }
}
