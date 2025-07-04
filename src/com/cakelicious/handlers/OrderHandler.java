package com.cakelicious.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.cakelicious.services.OrderService;
import com.cakelicious.models.Order;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OrderHandler implements HttpHandler {
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
                default:
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            System.err.println("Error in OrderHandler: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        
        // Check for specific order by ID
        String[] pathParts = path.split("/");
        if (pathParts.length > 3) {
            try {
                int orderId = Integer.parseInt(pathParts[3]);
                Order order = OrderService.getOrderById(orderId);
                if (order != null) {
                    sendResponse(exchange, 200, order.toJson());
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Order not found\"}");
                }
                return;
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid order ID\"}");
                return;
            }
        }
        
        List<Order> orders;
        
        // Check for query parameters
        if (query != null) {
            if (query.contains("userId=")) {
                try {
                    int userId = Integer.parseInt(extractQueryParam(query, "userId"));
                    orders = OrderService.getOrdersByUserId(userId);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid user ID\"}");
                    return;
                }
            } else if (query.contains("status=")) {
                String status = extractQueryParam(query, "status");
                orders = OrderService.getOrdersByStatus(status);
            } else {
                orders = OrderService.getAllOrders();
            }
        } else {
            orders = OrderService.getAllOrders();
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < orders.size(); i++) {
            json.append(orders.get(i).toJson());
            if (i < orders.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        sendResponse(exchange, 200, json.toString());
    }
    
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        
        try {
            int userId = Integer.parseInt(parseJsonField(body, "userId"));
            double totalAmount = Double.parseDouble(parseJsonField(body, "totalAmount"));
            double discountAmount = Double.parseDouble(parseJsonField(body, "discountAmount"));
            double deliveryFee = Double.parseDouble(parseJsonField(body, "deliveryFee"));
            String deliveryAddress = parseJsonField(body, "deliveryAddress");
            String specialInstructions = parseJsonField(body, "specialInstructions");
            String paymentMethod = parseJsonField(body, "paymentMethod");
            
            boolean success = OrderService.createOrder(userId, totalAmount, discountAmount, 
                                                     deliveryFee, deliveryAddress, 
                                                     specialInstructions, paymentMethod);
            
            if (success) {
                sendResponse(exchange, 201, "{\"message\":\"Order created successfully\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to create order\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        if (pathParts.length < 4) {
            sendResponse(exchange, 400, "{\"error\":\"Order ID required\"}");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(pathParts[3]);
            String body = readRequestBody(exchange);
            
            // Check if this is a status update or staff assignment
            if (body.contains("status")) {
                String status = parseJsonField(body, "status");
                boolean success = OrderService.updateOrderStatus(orderId, status);
                
                if (success) {
                    sendResponse(exchange, 200, "{\"message\":\"Order status updated successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Order not found or update failed\"}");
                }
            } else if (body.contains("assignedStaffId")) {
                int staffId = Integer.parseInt(parseJsonField(body, "assignedStaffId"));
                boolean success = OrderService.assignStaffToOrder(orderId, staffId);
                
                if (success) {
                    sendResponse(exchange, 200, "{\"message\":\"Staff assigned successfully\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Order not found or assignment failed\"}");
                }
            } else {
                sendResponse(exchange, 400, "{\"error\":\"Invalid update data\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid order ID\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
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
    
    private String extractQueryParam(String query, String paramName) {
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return "";
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}