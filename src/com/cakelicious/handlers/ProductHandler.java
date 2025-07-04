package com.cakelicious.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.cakelicious.services.ProductService;
import com.cakelicious.models.Product;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProductHandler implements HttpHandler {
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
            System.err.println("Error in ProductHandler: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        // Check if requesting a specific product by ID
        String[] pathParts = path.split("/");
        if (pathParts.length > 3) {
            try {
                int productId = Integer.parseInt(pathParts[3]);
                Product product = ProductService.getProductById(productId);
                if (product != null) {
                    sendResponse(exchange, 200, product.toJson());
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
                }
                return;
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid product ID\"}");
                return;
            }
        }
        
        // Get all products
        List<Product> products = ProductService.getAllProducts();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            json.append(products.get(i).toJson());
            if (i < products.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        sendResponse(exchange, 200, json.toString());
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        try {
            // Parse JSON manually (in production, use a proper JSON library)
            String name = parseJsonField(body, "name");
            String description = parseJsonField(body, "description");
            double price = Double.parseDouble(parseJsonField(body, "price"));
            String imageUrl = parseJsonField(body, "imageUrl");
            int categoryId = Integer.parseInt(parseJsonField(body, "categoryId"));
            int stockQuantity = Integer.parseInt(parseJsonField(body, "stockQuantity"));
            
            boolean success = ProductService.addProduct(name, description, price, imageUrl, categoryId, stockQuantity);
            
            if (success) {
                sendResponse(exchange, 201, "{\"message\":\"Product created successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to create product\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            sendResponse(exchange, 400, "{\"error\":\"Product ID required\"}");
            return;
        }
        
        try {
            int productId = Integer.parseInt(pathParts[3]);
            String body = readRequestBody(exchange);
            
            String name = parseJsonField(body, "name");
            String description = parseJsonField(body, "description");
            double price = Double.parseDouble(parseJsonField(body, "price"));
            String imageUrl = parseJsonField(body, "imageUrl");
            int categoryId = Integer.parseInt(parseJsonField(body, "categoryId"));
            int stockQuantity = Integer.parseInt(parseJsonField(body, "stockQuantity"));
            
            boolean success = ProductService.updateProduct(productId, name, description, price, imageUrl, categoryId, stockQuantity);
            
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Product updated successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Product not found or update failed\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid product ID\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            sendResponse(exchange, 400, "{\"error\":\"Product ID required\"}");
            return;
        }
        
        try {
            int productId = Integer.parseInt(pathParts[3]);
            boolean success = ProductService.deleteProduct(productId);
            
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Product deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Product not found or delete failed\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid product ID\"}");
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
            if (startIndex == -1) {
                // Try without quotes (for numbers)
                searchKey = "\"" + fieldName + "\":";
                startIndex = json.indexOf(searchKey);
                if (startIndex == -1) return "";
                startIndex += searchKey.length();
                int endIndex = json.indexOf(",", startIndex);
                if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
                return json.substring(startIndex, endIndex).trim();
            } else {
                startIndex += searchKey.length();
                int endIndex = json.indexOf("\"", startIndex);
                return json.substring(startIndex, endIndex);
            }
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