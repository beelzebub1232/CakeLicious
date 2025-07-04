package com.cakelicious.services;

import com.cakelicious.database.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    public static String authenticateUser(String email, String password) {
        // WARNING: Storing and comparing passwords in plaintext is highly insecure!
        // This is for demonstration purposes only. Use hashing (e.g., bcrypt) in a real application.
        String sql = "SELECT id, full_name, role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Return user data as a JSON string
                return String.format("{\"id\":%d, \"fullName\":\"%s\", \"role\":\"%s\"}",
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Authentication failed
    }
}