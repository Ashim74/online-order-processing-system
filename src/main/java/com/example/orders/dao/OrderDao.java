package com.example.orders.dao;

import com.example.orders.model.Order;

import java.util.List;

public interface OrderDao {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findAll();
    void deleteById(String orderId);
}
