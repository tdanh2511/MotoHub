package com.example.motohub.models;

public class CartItem {
    private int id;
    private int userId;
    private int motorbikeId;
    private int quantity;
    private Motorbike motorbike;

    public CartItem() {
    }

    public CartItem(int id, int userId, int motorbikeId, int quantity) {
        this.id = id;
        this.userId = userId;
        this.motorbikeId = motorbikeId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMotorbikeId() {
        return motorbikeId;
    }

    public void setMotorbikeId(int motorbikeId) {
        this.motorbikeId = motorbikeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Motorbike getMotorbike() {
        return motorbike;
    }

    public void setMotorbike(Motorbike motorbike) {
        this.motorbike = motorbike;
    }

    public double getTotalPrice() {
        return motorbike != null ? motorbike.getPrice() * quantity : 0;
    }
}
