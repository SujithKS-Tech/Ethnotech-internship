package com.expensetracker.db;

import com.expensetracker.config.AppConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private final AppConfig config;

    public Database(AppConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.dbUrl(),
                config.dbUsername(),
                config.dbPassword()
        );
    }

    public void initialize() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(120) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    amount DECIMAL(12, 2) NOT NULL,
                    expense_date DATE NOT NULL,
                    payment_method VARCHAR(40) NOT NULL,
                    notes VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_expense_date (expense_date),
                    INDEX idx_category (category)
                )
                """;

        String createBudgetTable = """
                CREATE TABLE IF NOT EXISTS monthly_budget (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    year_month CHAR(7) NOT NULL UNIQUE,
                    budget DECIMAL(12, 2) NOT NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createExpensesTable);
            statement.executeUpdate(createBudgetTable);
        }
    }
}

