package com.cakelicious.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import com.cakelicious.services.UserService;

public class AuthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();

            // Simple JSON parsing (not robust, use a library like Gson/Jackson for real apps)
            String email = parseJsonField(body, "email");
            String password = parseJsonField(body, "password");

            String userJson = UserService.authenticateUser(email, password);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            if (userJson != null) {
                sendResponse(exchange, 200, userJson.getBytes(StandardCharsets.UTF_8));
            } else {
                String response = "{\"error\":\"Invalid credentials\"}";
                sendResponse(exchange, 401, response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
        }
    }

    private String parseJsonField(String json, String fieldName) {
        try {
            String searchKey = "\"" + fieldName + "\":\"";
            int startIndex = json.indexOf(searchKey) + searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "";
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}