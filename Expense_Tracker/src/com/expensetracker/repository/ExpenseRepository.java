package com.expensetracker.repository;

import com.expensetracker.db.Database;
import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.DashboardSummary;
import com.expensetracker.model.Expense;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class ExpenseRepository {
    private final Database database;

    public ExpenseRepository(Database database) {
        this.database = database;
    }

    public Expense createExpense(
            String title,
            String category,
            BigDecimal amount,
            LocalDate expenseDate,
            String paymentMethod,
            String notes
    ) throws SQLException {
        String sql = """
                INSERT INTO expenses (title, category, amount, expense_date, payment_method, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setBigDecimal(3, amount);
            statement.setDate(4, Date.valueOf(expenseDate));
            statement.setString(5, paymentMethod);
            statement.setString(6, notes);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return findExpenseById(keys.getLong(1));
                }
            }
        }

        throw new SQLException("Expense could not be created.");
    }

    public Expense updateExpense(
            long id,
            String title,
            String category,
            BigDecimal amount,
            LocalDate expenseDate,
            String paymentMethod,
            String notes
    ) throws SQLException {
        String sql = """
                UPDATE expenses
                SET title = ?, category = ?, amount = ?, expense_date = ?, payment_method = ?, notes = ?
                WHERE id = ?
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setBigDecimal(3, amount);
            statement.setDate(4, Date.valueOf(expenseDate));
            statement.setString(5, paymentMethod);
            statement.setString(6, notes);
            statement.setLong(7, id);

            if (statement.executeUpdate() == 0) {
                throw new NoSuchElementException("Expense not found.");
            }
        }

        return findExpenseById(id);
    }

    public void deleteExpense(long id) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            if (statement.executeUpdate() == 0) {
                throw new NoSuchElementException("Expense not found.");
            }
        }
    }

    public Expense findExpenseById(long id) throws SQLException {
        String sql = """
                SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                FROM expenses
                WHERE id = ?
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapExpense(resultSet);
                }
            }
        }

        throw new NoSuchElementException("Expense not found.");
    }

    public List<Expense> findExpenses(YearMonth month, String categoryFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                FROM expenses
                WHERE expense_date >= ? AND expense_date < ?
                """);

        boolean filterByCategory = categoryFilter != null
                && !categoryFilter.isBlank()
                && !"All".equalsIgnoreCase(categoryFilter);

        if (filterByCategory) {
            sql.append(" AND category = ?");
        }

        sql.append(" ORDER BY expense_date DESC, id DESC");

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setDate(1, Date.valueOf(month.atDay(1)));
            statement.setDate(2, Date.valueOf(month.plusMonths(1).atDay(1)));

            if (filterByCategory) {
                statement.setString(3, categoryFilter);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Expense> expenses = new ArrayList<>();
                while (resultSet.next()) {
                    expenses.add(mapExpense(resultSet));
                }
                return expenses;
            }
        }
    }

    public BigDecimal upsertMonthlyBudget(YearMonth month, BigDecimal budget) throws SQLException {
        String sql = """
                INSERT INTO monthly_budget (year_month, budget)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE budget = VALUES(budget)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, month.toString());
            statement.setBigDecimal(2, budget);
            statement.executeUpdate();
        }

        return budget;
    }

    public DashboardSummary loadDashboard(YearMonth month) throws SQLException {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        try (Connection connection = database.getConnection()) {
            BigDecimal monthlyBudget = findMonthlyBudget(connection, month);
            BigDecimal totalSpent = BigDecimal.ZERO;
            int expenseCount = 0;
            String topCategory = "No expenses yet";
            List<CategoryTotal> breakdown = new ArrayList<>();
            List<Expense> recentExpenses = new ArrayList<>();

            String summarySql = """
                    SELECT COALESCE(SUM(amount), 0) AS total_spent, COUNT(*) AS expense_count
                    FROM expenses
                    WHERE expense_date >= ? AND expense_date < ?
                    """;

            try (PreparedStatement statement = connection.prepareStatement(summarySql)) {
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        totalSpent = resultSet.getBigDecimal("total_spent");
                        expenseCount = resultSet.getInt("expense_count");
                    }
                }
            }

            String topCategorySql = """
                    SELECT category, SUM(amount) AS total_amount
                    FROM expenses
                    WHERE expense_date >= ? AND expense_date < ?
                    GROUP BY category
                    ORDER BY total_amount DESC, category ASC
                    LIMIT 1
                    """;

            try (PreparedStatement statement = connection.prepareStatement(topCategorySql)) {
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        topCategory = resultSet.getString("category");
                    }
                }
            }

            String breakdownSql = """
                    SELECT category, SUM(amount) AS total_amount
                    FROM expenses
                    WHERE expense_date >= ? AND expense_date < ?
                    GROUP BY category
                    ORDER BY total_amount DESC, category ASC
                    """;

            try (PreparedStatement statement = connection.prepareStatement(breakdownSql)) {
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        breakdown.add(new CategoryTotal(
                                resultSet.getString("category"),
                                resultSet.getBigDecimal("total_amount")
                        ));
                    }
                }
            }

            String recentSql = """
                    SELECT id, title, category, amount, expense_date, payment_method, COALESCE(notes, '') AS notes
                    FROM expenses
                    WHERE expense_date >= ? AND expense_date < ?
                    ORDER BY expense_date DESC, id DESC
                    LIMIT 5
                    """;

            try (PreparedStatement statement = connection.prepareStatement(recentSql)) {
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        recentExpenses.add(mapExpense(resultSet));
                    }
                }
            }

            return new DashboardSummary(
                    month.toString(),
                    monthlyBudget,
                    totalSpent,
                    monthlyBudget.subtract(totalSpent),
                    expenseCount,
                    topCategory,
                    breakdown,
                    recentExpenses
            );
        }
    }

    private BigDecimal findMonthlyBudget(Connection connection, YearMonth month) throws SQLException {
        String sql = "SELECT budget FROM monthly_budget WHERE year_month = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, month.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("budget");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private Expense mapExpense(ResultSet resultSet) throws SQLException {
        return new Expense(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("category"),
                resultSet.getBigDecimal("amount"),
                resultSet.getDate("expense_date").toLocalDate(),
                resultSet.getString("payment_method"),
                resultSet.getString("notes")
        );
    }
}

