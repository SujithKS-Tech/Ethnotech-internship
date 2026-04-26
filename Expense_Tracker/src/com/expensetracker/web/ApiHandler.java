package com.expensetracker.web;

import com.expensetracker.config.AppConfig;
import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.DashboardSummary;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.util.HttpUtils;
import com.expensetracker.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public final class ApiHandler implements HttpHandler {
    private final ExpenseRepository repository;
    private final AppConfig config;

    public ApiHandler(ExpenseRepository repository, AppConfig config) {
        this.repository = repository;
        this.config = config;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method) && "/api/health".equals(path)) {
                HttpUtils.sendJson(exchange, 200, "{\"message\":\"ok\"}");
                return;
            }

            if ("GET".equalsIgnoreCase(method) && "/api/dashboard".equals(path)) {
                handleDashboard(exchange);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && "/api/expenses".equals(path)) {
                handleListExpenses(exchange);
                return;
            }

            if ("POST".equalsIgnoreCase(method) && "/api/expenses".equals(path)) {
                handleCreateExpense(exchange);
                return;
            }

            if ("PUT".equalsIgnoreCase(method) && path.startsWith("/api/expenses/")) {
                handleUpdateExpense(exchange, extractId(path, "/api/expenses/"));
                return;
            }

            if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/api/expenses/")) {
                handleDeleteExpense(exchange, extractId(path, "/api/expenses/"));
                return;
            }

            if ("POST".equalsIgnoreCase(method) && "/api/budget".equals(path)) {
                handleUpsertBudget(exchange);
                return;
            }

            HttpUtils.sendJson(exchange, 404, "{\"message\":\"Endpoint not found.\"}");
        } catch (IllegalArgumentException exception) {
            HttpUtils.sendJson(exchange, 400, messageJson(exception.getMessage()));
        } catch (NoSuchElementException exception) {
            HttpUtils.sendJson(exchange, 404, messageJson(exception.getMessage()));
        } catch (Exception exception) {
            exception.printStackTrace();
            HttpUtils.sendJson(exchange, 500, messageJson("Something went wrong on the server."));
        }
    }

    private void handleDashboard(HttpExchange exchange) throws Exception {
        Map<String, String> query = HttpUtils.parseQuery(exchange.getRequestURI().getRawQuery());
        YearMonth month = parseMonth(query.get("month"));
        DashboardSummary summary = repository.loadDashboard(month);
        HttpUtils.sendJson(exchange, 200, dashboardJson(summary));
    }

    private void handleListExpenses(HttpExchange exchange) throws Exception {
        Map<String, String> query = HttpUtils.parseQuery(exchange.getRequestURI().getRawQuery());
        YearMonth month = parseMonth(query.get("month"));
        String category = query.getOrDefault("category", "All");
        List<Expense> expenses = repository.findExpenses(month, category);

        StringBuilder builder = new StringBuilder();
        builder.append("{\"items\":[");
        for (int index = 0; index < expenses.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(expenseJson(expenses.get(index)));
        }
        builder.append("]}");

        HttpUtils.sendJson(exchange, 200, builder.toString());
    }

    private void handleCreateExpense(HttpExchange exchange) throws Exception {
        Map<String, String> form = HttpUtils.parseFormBody(exchange.getRequestBody());
        Expense expense = repository.createExpense(
                requiredText(form.get("title"), "Title", 120),
                requiredText(form.get("category"), "Category", 50),
                positiveAmount(form.get("amount")),
                requiredDate(form.get("expenseDate")),
                requiredText(form.get("paymentMethod"), "Payment method", 40),
                optionalText(form.get("notes"), 255)
        );

        HttpUtils.sendJson(exchange, 201, "{\"message\":\"Expense added.\",\"item\":" + expenseJson(expense) + "}");
    }

    private void handleUpdateExpense(HttpExchange exchange, long id) throws Exception {
        Map<String, String> form = HttpUtils.parseFormBody(exchange.getRequestBody());
        Expense expense = repository.updateExpense(
                id,
                requiredText(form.get("title"), "Title", 120),
                requiredText(form.get("category"), "Category", 50),
                positiveAmount(form.get("amount")),
                requiredDate(form.get("expenseDate")),
                requiredText(form.get("paymentMethod"), "Payment method", 40),
                optionalText(form.get("notes"), 255)
        );

        HttpUtils.sendJson(exchange, 200, "{\"message\":\"Expense updated.\",\"item\":" + expenseJson(expense) + "}");
    }

    private void handleDeleteExpense(HttpExchange exchange, long id) throws Exception {
        repository.deleteExpense(id);
        HttpUtils.sendJson(exchange, 200, "{\"message\":\"Expense deleted.\"}");
    }

    private void handleUpsertBudget(HttpExchange exchange) throws Exception {
        Map<String, String> form = HttpUtils.parseFormBody(exchange.getRequestBody());
        YearMonth month = parseMonth(form.get("month"));
        BigDecimal budget = positiveOrZeroAmount(form.get("budget"));
        repository.upsertMonthlyBudget(month, budget);
        HttpUtils.sendJson(exchange, 200, "{\"message\":\"Budget saved.\"}");
    }

    private String dashboardJson(DashboardSummary summary) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("\"month\":").append(JsonUtil.quote(summary.month())).append(',');
        builder.append("\"currency\":").append(JsonUtil.quote(config.currencyCode())).append(',');
        builder.append("\"summary\":{");
        builder.append("\"monthlyBudget\":").append(JsonUtil.decimal(summary.monthlyBudget())).append(',');
        builder.append("\"totalSpent\":").append(JsonUtil.decimal(summary.totalSpent())).append(',');
        builder.append("\"remainingBudget\":").append(JsonUtil.decimal(summary.remainingBudget())).append(',');
        builder.append("\"expenseCount\":").append(summary.expenseCount()).append(',');
        builder.append("\"topCategory\":").append(JsonUtil.quote(summary.topCategory()));
        builder.append("},");
        builder.append("\"categoryBreakdown\":[");

        List<CategoryTotal> breakdown = summary.categoryBreakdown();
        for (int index = 0; index < breakdown.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(categoryTotalJson(breakdown.get(index)));
        }

        builder.append("],");
        builder.append("\"recentExpenses\":[");

        List<Expense> recentExpenses = summary.recentExpenses();
        for (int index = 0; index < recentExpenses.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(expenseJson(recentExpenses.get(index)));
        }

        builder.append("]}");
        return builder.toString();
    }

    private String expenseJson(Expense expense) {
        return "{"
                + "\"id\":" + expense.id() + ','
                + "\"title\":" + JsonUtil.quote(expense.title()) + ','
                + "\"category\":" + JsonUtil.quote(expense.category()) + ','
                + "\"amount\":" + JsonUtil.decimal(expense.amount()) + ','
                + "\"expenseDate\":" + JsonUtil.quote(expense.expenseDate().toString()) + ','
                + "\"paymentMethod\":" + JsonUtil.quote(expense.paymentMethod()) + ','
                + "\"notes\":" + JsonUtil.quote(expense.notes())
                + "}";
    }

    private String categoryTotalJson(CategoryTotal categoryTotal) {
        return "{"
                + "\"category\":" + JsonUtil.quote(categoryTotal.category()) + ','
                + "\"amount\":" + JsonUtil.decimal(categoryTotal.amount())
                + "}";
    }

    private String messageJson(String message) {
        return "{\"message\":" + JsonUtil.quote(message) + "}";
    }

    private YearMonth parseMonth(String value) {
        if (value == null || value.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Month must be in YYYY-MM format.");
        }
    }

    private LocalDate requiredDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Expense date is required.");
        }

        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Expense date must be in YYYY-MM-DD format.");
        }
    }

    private BigDecimal positiveAmount(String value) {
        BigDecimal amount = parseAmount(value, "Amount is required.");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        return amount;
    }

    private BigDecimal positiveOrZeroAmount(String value) {
        BigDecimal amount = parseAmount(value, "Budget is required.");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget cannot be negative.");
        }
        return amount;
    }

    private BigDecimal parseAmount(String value, String emptyMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(emptyMessage);
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
    }

    private String requiredText(String value, String label, int maxLength) {
        String sanitized = optionalText(value, maxLength);
        if (sanitized.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return sanitized;
    }

    private String optionalText(String value, int maxLength) {
        String sanitized = value == null ? "" : value.trim();
        if (sanitized.length() > maxLength) {
            throw new IllegalArgumentException("Value is too long. Maximum length is " + maxLength + " characters.");
        }
        return sanitized;
    }

    private long extractId(String path, String prefix) {
        try {
            return Long.parseLong(path.substring(prefix.length()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid expense id.");
        }
    }
}

