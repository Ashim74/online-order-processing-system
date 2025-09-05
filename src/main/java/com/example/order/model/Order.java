package com.example.order.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private Customer customer;
    private List<Product> products = new ArrayList<>();
    private String status;

    public Order() {}

    public Order(int orderId, Customer customer, List<Product> products, String status) {
        this.orderId = orderId;
        this.customer = customer;
        this.products = products;
        this.status = status;
    }

    public Order(Customer customer, List<Product> products, String status) {
        this.customer = customer;
        this.products = products;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double calculateTotalValue() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customer=" + customer +
                ", products=" + products +
                ", status='" + status + '\'' +
                '}';
    }
}
