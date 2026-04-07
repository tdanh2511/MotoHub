package com.example.motohub.models;

public class Order {
    private int id;
    private int userId;
    private int motorbikeId;
    private String customerName;
    private String motorbikeName;
    private double price;
    private int quantity;
    private String orderDate;
    private String status;
    private String phone;
    private String address;

    public Order() {
    }

    public Order(int id, int userId, int motorbikeId, String customerName, String motorbikeName,
                 double price, int quantity, String orderDate, String status, String phone, String address) {
        this.id = id;
        this.userId = userId;
        this.motorbikeId = motorbikeId;
        this.customerName = customerName;
        this.motorbikeName = motorbikeName;
        this.price = price;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.status = status;
        this.phone = phone;
        this.address = address;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMotorbikeName() {
        return motorbikeName;
    }

    public void setMotorbikeName(String motorbikeName) {
        this.motorbikeName = motorbikeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
