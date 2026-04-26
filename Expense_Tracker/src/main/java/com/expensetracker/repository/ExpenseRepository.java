package com.expensetracker.repository;

import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.DashboardData;
import com.expensetracker.model.DashboardSummary;
import com.expensetracker.model.Expense;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepository {
    private final JdbcTemplate jdbcTemplate;

    public ExpenseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Expense createExpense(
            String title,
            String category,
            BigDecimal amount,
            LocalDate expenseDate,
            String paymentMethod,
            String notes
    ) {
        String sql = """
                INSERT INTO expenses (title, category, amount, expense_date, payment_method, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setBigDecimal(3, amount);
            statement.setDate(4, Date.valueOf(expenseDate));
            statement.setString(5, paymentMethod);
            statement.setString(6, notes);
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Expense could not be created.");
        }

        return findExpenseById(key.longValue()).orElseThrow();
    }

    public boolean updateExpense(
            long id,
            String title,
            String category,
            BigDecimal amount,
            LocalDate expenseDate,
            String paymentMethod,
            String notes
    ) {
        String sql = """
                UPDATE expenses
                SET title = ?, category = ?, amount = ?, expense_date = ?, payment_method = ?, notes = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(
                sql,
                title,
                category,
                amount,
                Date.valueOf(expenseDate),
                paymentMethod,
                notes,
                id
        );

        return updated > 0;
    }

    public boolean deleteExpense(long id) {
        int deleted = jdbcTemplate.update("DELETE FROM expenses WHERE id = ?", id);
        return deleted > 0;
    }

    public Optional<Expense> findExpenseById(long id) {
        String sql = """
                SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                FROM expenses
                WHERE id = ?
                """;

        List<Expense> items = jdbcTemplate.query(sql, (resultSet, rowNum) -> new Expense(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("category"),
                resultSet.getBigDecimal("amount"),
                resultSet.getDate("expense_date").toLocalDate(),
                resultSet.getString("payment_method"),
                resultSet.getString("notes")
        ), id);

        return items.stream().findFirst();
    }

    public List<Expense> findExpenses(YearMonth month, String categoryFilter) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                """);

        List<Object> params = new ArrayList<>();
        params.add(Date.valueOf(month.atDay(1)));
        params.add(Date.valueOf(month.plusMonths(1).atDay(1)));

        boolean filterByCategory = categoryFilter != null
                && !categoryFilter.isBlank()
                && !"All".equalsIgnoreCase(categoryFilter);

        if (filterByCategory) {
            sql.append(" AND category = ?");
            params.add(categoryFilter);
        }

        sql.append(" ORDER BY expense_date DESC, id DESC");

        return jdbcTemplate.query(sql.toString(), (resultSet, rowNum) -> new Expense(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("category"),
                resultSet.getBigDecimal("amount"),
                resultSet.getDate("expense_date").toLocalDate(),
                resultSet.getString("payment_method"),
                resultSet.getString("notes")
        ), params.toArray());
    }

    public void upsertMonthlyBudget(YearMonth month, BigDecimal budget) {
        String sql = """
                INSERT INTO monthly_budget (`year_month`, budget)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE budget = VALUES(budget)
                """;
        jdbcTemplate.update(sql, month.toString(), budget);
    }

    public DashboardData loadDashboard(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        BigDecimal monthlyBudget = findMonthlyBudget(month);
        BigDecimal totalSpent = BigDecimal.ZERO;
        int expenseCount = 0;
        String topCategory = "No expenses yet";

        String summarySql = """
                SELECT COALESCE(SUM(amount), 0) AS total_spent, COUNT(*) AS expense_count
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                """;

        List<Object[]> summaryRows = jdbcTemplate.query(summarySql, (resultSet, rowNum) -> new Object[]{
                resultSet.getBigDecimal("total_spent"),
                resultSet.getInt("expense_count")
        }, Date.valueOf(startDate), Date.valueOf(endDate));

        if (!summaryRows.isEmpty()) {
            Object[] row = summaryRows.get(0);
            totalSpent = (BigDecimal) row[0];
            expenseCount = (Integer) row[1];
        }

        String topCategorySql = """
                SELECT category, SUM(amount) AS total_amount
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                GROUP BY category
                ORDER BY total_amount DESC, category ASC
                LIMIT 1
                """;

        List<String> topCategoryRows = jdbcTemplate.query(topCategorySql, (resultSet, rowNum) ->
                        resultSet.getString("category"),
                Date.valueOf(startDate),
                Date.valueOf(endDate)
        );

        if (!topCategoryRows.isEmpty()) {
            topCategory = topCategoryRows.get(0);
        }

        String breakdownSql = """
                SELECT category, SUM(amount) AS total_amount
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                GROUP BY category
                ORDER BY total_amount DESC, category ASC
                """;

        List<CategoryTotal> breakdown = jdbcTemplate.query(breakdownSql, (resultSet, rowNum) -> new CategoryTotal(
                resultSet.getString("category"),
                resultSet.getBigDecimal("total_amount")
        ), Date.valueOf(startDate), Date.valueOf(endDate));

        String recentSql = """
                SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                ORDER BY expense_date DESC, id DESC
                LIMIT 5
                """;

        List<Expense> recentExpenses = jdbcTemplate.query(recentSql, (resultSet, rowNum) -> new Expense(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("category"),
                resultSet.getBigDecimal("amount"),
                resultSet.getDate("expense_date").toLocalDate(),
                resultSet.getString("payment_method"),
                resultSet.getString("notes")
        ), Date.valueOf(startDate), Date.valueOf(endDate));

        DashboardSummary summary = new DashboardSummary(
                monthlyBudget,
                totalSpent,
                monthlyBudget.subtract(totalSpent),
                expenseCount,
                topCategory
        );

        return new DashboardData(summary, breakdown, recentExpenses);
    }

    private BigDecimal findMonthlyBudget(YearMonth month) {
        String sql = "SELECT budget FROM monthly_budget WHERE `year_month` = ?";
        try {
            BigDecimal budget = jdbcTemplate.queryForObject(sql, BigDecimal.class, month.toString());
            return budget == null ? BigDecimal.ZERO : budget;
        } catch (EmptyResultDataAccessException exception) {
            return BigDecimal.ZERO;
        }
    }
}
