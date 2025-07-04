package com.cakelicious.services;

import com.cakelicious.database.DatabaseConnector;
import com.cakelicious.models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    
    public static List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = TRUE ORDER BY name";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image_url"),
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }
    
    public static Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image_url"),
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean addCategory(String name, String description, String imageUrl) {
        String sql = "INSERT INTO categories (name, description, image_url) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, imageUrl);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateCategory(int id, String name, String description, String imageUrl) {
        String sql = "UPDATE categories SET name = ?, description = ?, image_url = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, imageUrl);
            pstmt.setInt(4, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean deleteCategory(int id) {
        String sql = "UPDATE categories SET is_active = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}