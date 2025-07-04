package com.cakelicious.models;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int categoryId;
    private String categoryName;
    private int stockQuantity;

    // Default constructor
    public Product() {}

    // Constructor with all fields
    public Product(int id, String name, String description, double price, 
                   String imageUrl, int categoryId, String categoryName, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"price\":%.2f," +
            "\"imageUrl\":\"%s\",\"categoryId\":%d,\"categoryName\":\"%s\",\"stockQuantity\":%d}",
            id, name.replace("\"", "\\\""), description.replace("\"", "\\\""), price,
            imageUrl != null ? imageUrl.replace("\"", "\\\"") : "",
            categoryId, categoryName != null ? categoryName.replace("\"", "\\\"") : "", stockQuantity
        );
    }
}