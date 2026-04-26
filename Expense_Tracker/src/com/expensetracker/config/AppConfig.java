package com.expensetracker.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record AppConfig(
        String dbUrl,
        String dbUsername,
        String dbPassword,
        int serverPort,
        String currencyCode
) {
    public static AppConfig load() throws IOException {
        Properties properties = new Properties();
        Path configPath = Path.of("config", "app.properties");

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            }
        }

        String dbUrl = properties.getProperty(
                "db.url",
                "jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&serverTimezone=Asia/Kolkata"
        );
        String dbUsername = properties.getProperty("db.username", "root");
        String dbPassword = properties.getProperty("db.password", "Ajay.1@123");
        int serverPort = Integer.parseInt(properties.getProperty("server.port", "8080"));
        String currencyCode = properties.getProperty("app.currency", "INR");

        return new AppConfig(dbUrl, dbUsername, dbPassword, serverPort, currencyCode);
    }
}

