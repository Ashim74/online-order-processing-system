package com.example.order.dao;

import com.example.order.model.Order;

public interface OrderDAO {
    Order placeOrder(Order order);
    Order getOrder(int orderId);
    void updateOrderStatus(int orderId, String status);
    void deleteOrder(int orderId);
    double calculateTotalValue(int orderId);
}
