package com.cakelicious.models;

import java.sql.Timestamp;

public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String role;
    private String address;
    private String dateOfBirth;
    private String profileImage;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public User() {}

    // Constructor with essential fields
    public User(int id, String email, String fullName, String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Full constructor
    public User(int id, String email, String password, String fullName, String phone, 
                String role, String address, String dateOfBirth, String profileImage, 
                boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.profileImage = profileImage;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"email\":\"%s\",\"fullName\":\"%s\",\"phone\":\"%s\"," +
            "\"role\":\"%s\",\"address\":\"%s\",\"isActive\":%b}",
            id, 
            email != null ? email.replace("\"", "\\\"") : "",
            fullName != null ? fullName.replace("\"", "\\\"") : "",
            phone != null ? phone.replace("\"", "\\\"") : "",
            role != null ? role.replace("\"", "\\\"") : "",
            address != null ? address.replace("\"", "\\\"") : "",
            isActive
        );
    }
}