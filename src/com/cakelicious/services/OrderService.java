package com.cakelicious.services;

import com.cakelicious.database.DatabaseConnector;
import com.cakelicious.models.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService {
    
    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    public static List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    public static List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by status: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    public static Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createOrder(int userId, double totalAmount, double discountAmount, 
                                    double deliveryFee, String deliveryAddress, 
                                    String specialInstructions, String paymentMethod) {
        String orderNumber = generateOrderNumber();
        double finalAmount = totalAmount - discountAmount + deliveryFee;
        
        String sql = "INSERT INTO orders (user_id, order_number, total_amount, discount_amount, " +
                    "delivery_fee, final_amount, delivery_address, special_instructions, payment_method) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, orderNumber);
            pstmt.setDouble(3, totalAmount);
            pstmt.setDouble(4, discountAmount);
            pstmt.setDouble(5, deliveryFee);
            pstmt.setDouble(6, finalAmount);
            pstmt.setString(7, deliveryAddress);
            pstmt.setString(8, specialInstructions);
            pstmt.setString(9, paymentMethod);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean assignStaffToOrder(int orderId, int staffId) {
        String sql = "UPDATE orders SET assigned_staff_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning staff to order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private static Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setDeliveryFee(rs.getDouble("delivery_fee"));
        order.setFinalAmount(rs.getDouble("final_amount"));
        order.setStatus(rs.getString("status"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setDeliveryDate(rs.getDate("delivery_date"));
        order.setDeliveryTime(rs.getTime("delivery_time"));
        order.setSpecialInstructions(rs.getString("special_instructions"));
        
        int assignedStaffId = rs.getInt("assigned_staff_id");
        if (!rs.wasNull()) {
            order.setAssignedStaffId(assignedStaffId);
        }
        
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return order;
    }
    
    private static String generateOrderNumber() {
        return "CL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}