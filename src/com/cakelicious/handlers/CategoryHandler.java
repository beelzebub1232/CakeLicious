package com.cakelicious.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.cakelicious.services.CategoryService;
import com.cakelicious.models.Category;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CategoryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        // Add CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            System.err.println("Error in CategoryHandler: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        // Check if requesting a specific category by ID
        String[] pathParts = path.split("/");
        if (pathParts.length > 3) {
            try {
                int categoryId = Integer.parseInt(pathParts[3]);
                Category category = CategoryService.getCategoryById(categoryId);
                if (category != null) {
                    sendResponse(exchange, 200, category.toJson());
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Category not found\"}");
                }
                return;
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid category ID\"}");
                return;
            }
        }
        
        // Get all categories
        List<Category> categories = CategoryService.getAllCategories();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            json.append(categories.get(i).toJson());
            if (i < categories.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        sendResponse(exchange, 200, json.toString());
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        try {
            String name = parseJsonField(body, "name");
            String description = parseJsonField(body, "description");
            String imageUrl = parseJsonField(body, "imageUrl");
            
            boolean success = CategoryService.addCategory(name, description, imageUrl);
            
            if (success) {
                sendResponse(exchange, 201, "{\"message\":\"Category created successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to create category\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            sendResponse(exchange, 400, "{\"error\":\"Category ID required\"}");
            return;
        }
        
        try {
            int categoryId = Integer.parseInt(pathParts[3]);
            String body = readRequestBody(exchange);
            
            String name = parseJsonField(body, "name");
            String description = parseJsonField(body, "description");
            String imageUrl = parseJsonField(body, "imageUrl");
            
            boolean success = CategoryService.updateCategory(categoryId, name, description, imageUrl);
            
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Category updated successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Category not found or update failed\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid category ID\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            sendResponse(exchange, 400, "{\"error\":\"Category ID required\"}");
            return;
        }
        
        try {
            int categoryId = Integer.parseInt(pathParts[3]);
            boolean success = CategoryService.deleteCategory(categoryId);
            
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Category deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Category not found or delete failed\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid category ID\"}");
        }
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    private String parseJsonField(String json, String fieldName) {
        try {
            String searchKey = "\"" + fieldName + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "";
        }
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}