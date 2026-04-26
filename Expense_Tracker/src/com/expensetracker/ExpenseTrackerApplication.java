package com.expensetracker;

import com.expensetracker.config.AppConfig;
import com.expensetracker.db.Database;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.web.ApiHandler;
import com.expensetracker.web.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public final class ExpenseTrackerApplication {
    private ExpenseTrackerApplication() {
    }

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.load();
        Database database = new Database(config);
        database.initialize();

        ExpenseRepository repository = new ExpenseRepository(database);
        HttpServer server = HttpServer.create(new InetSocketAddress(config.serverPort()), 0);
        server.createContext("/api", new ApiHandler(repository, config));
        server.createContext("/", new StaticFileHandler(Path.of("web")));
        server.setExecutor(Executors.newFixedThreadPool(12));
        server.start();

        System.out.println("Expense Tracker running on http://localhost:" + config.serverPort());
        System.out.println("Connected to: " + config.dbUrl());
    }
}

