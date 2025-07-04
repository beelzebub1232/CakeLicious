package com.cakelicious.models;

import java.sql.Timestamp;

public class Category {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isActive;
    private Timestamp createdAt;

    // Default constructor
    public Category() {}

    // Constructor with essential fields
    public Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Full constructor
    public Category(int id, String name, String description, String imageUrl, 
                   boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"imageUrl\":\"%s\",\"isActive\":%b}",
            id,
            name != null ? name.replace("\"", "\\\"") : "",
            description != null ? description.replace("\"", "\\\"") : "",
            imageUrl != null ? imageUrl.replace("\"", "\\\"") : "",
            isActive
        );
    }
}