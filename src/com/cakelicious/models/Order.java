package com.cakelicious.models;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;

public class Order {
    private int id;
    private int userId;
    private String orderNumber;
    private double totalAmount;
    private double discountAmount;
    private double deliveryFee;
    private double finalAmount;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String deliveryAddress;
    private Date deliveryDate;
    private Time deliveryTime;
    private String specialInstructions;
    private Integer assignedStaffId;
    private Timestamp orderDate;
    private Timestamp updatedAt;

    // Default constructor
    public Order() {}

    // Constructor with essential fields
    public Order(int id, int userId, String orderNumber, double finalAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.finalAmount = finalAmount;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public Time getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Time deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Integer getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Integer assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"userId\":%d,\"orderNumber\":\"%s\",\"totalAmount\":%.2f," +
            "\"discountAmount\":%.2f,\"deliveryFee\":%.2f,\"finalAmount\":%.2f," +
            "\"status\":\"%s\",\"paymentStatus\":\"%s\",\"paymentMethod\":\"%s\"," +
            "\"deliveryAddress\":\"%s\",\"specialInstructions\":\"%s\"," +
            "\"assignedStaffId\":%s,\"orderDate\":\"%s\"}",
            id, userId, 
            orderNumber != null ? orderNumber.replace("\"", "\\\"") : "",
            totalAmount, discountAmount, deliveryFee, finalAmount,
            status != null ? status.replace("\"", "\\\"") : "",
            paymentStatus != null ? paymentStatus.replace("\"", "\\\"") : "",
            paymentMethod != null ? paymentMethod.replace("\"", "\\\"") : "",
            deliveryAddress != null ? deliveryAddress.replace("\"", "\\\"") : "",
            specialInstructions != null ? specialInstructions.replace("\"", "\\\"") : "",
            assignedStaffId != null ? assignedStaffId.toString() : "null",
            orderDate != null ? orderDate.toString() : ""
        );
    }
}