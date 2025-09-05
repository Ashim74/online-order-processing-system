package com.example.orders.model;

import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private List<Product> products;
    private OrderStatus status;

    public Order() {
    }

    public Order(String orderId, Customer customer, List<Product> products, OrderStatus status) {
        this.orderId = orderId;
        this.customer = customer;
        this.products = products;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
