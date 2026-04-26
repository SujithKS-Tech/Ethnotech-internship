package com.expensetracker.web;

import com.expensetracker.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StaticFileHandler implements HttpHandler {
    private final Path webRoot;

    public StaticFileHandler(Path webRoot) {
        this.webRoot = webRoot.toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestedPath = URLDecoder.decode(exchange.getRequestURI().getPath(), StandardCharsets.UTF_8);
        Path target;

        if ("/".equals(requestedPath)) {
            target = webRoot.resolve("index.html");
        } else {
            target = webRoot.resolve(requestedPath.substring(1)).normalize();
        }

        if (!target.startsWith(webRoot)) {
            HttpUtils.sendBytes(exchange, 403, "text/plain; charset=utf-8", "Forbidden".getBytes(StandardCharsets.UTF_8));
            return;
        }

        if (Files.isDirectory(target)) {
            target = target.resolve("index.html");
        }

        if (!Files.exists(target)) {
            HttpUtils.sendBytes(exchange, 404, "text/plain; charset=utf-8", "File not found".getBytes(StandardCharsets.UTF_8));
            return;
        }

        HttpUtils.sendBytes(exchange, 200, contentType(target), Files.readAllBytes(target));
    }

    private String contentType(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (fileName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (fileName.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (fileName.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }

        return "application/octet-stream";
    }
}

