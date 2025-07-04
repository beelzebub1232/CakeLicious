package com.cakelicious.services;

import com.cakelicious.database.DatabaseConnector;
import com.cakelicious.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.description, p.price, p.image_url, " +
                    "p.category_id, c.name as category_name, p.stock_quantity " +
                    "FROM products p LEFT JOIN categories c ON p.category_id = c.id " +
                    "ORDER BY p.name";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("image_url"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getInt("stock_quantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }
    
    public static Product getProductById(int id) {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.image_url, " +
                    "p.category_id, c.name as category_name, p.stock_quantity " +
                    "FROM products p LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("image_url"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getInt("stock_quantity")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean addProduct(String name, String description, double price, 
                                   String imageUrl, int categoryId, int stockQuantity) {
        String sql = "INSERT INTO products (name, description, price, image_url, category_id, stock_quantity) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, imageUrl);
            pstmt.setInt(5, categoryId);
            pstmt.setInt(6, stockQuantity);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateProduct(int id, String name, String description, double price, 
                                      String imageUrl, int categoryId, int stockQuantity) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, " +
                    "image_url = ?, category_id = ?, stock_quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, imageUrl);
            pstmt.setInt(5, categoryId);
            pstmt.setInt(6, stockQuantity);
            pstmt.setInt(7, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}