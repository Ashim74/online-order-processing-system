package com.example.orders.service;

import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void placeOrder(Order order);
    Order getOrder(String orderId);
    List<Order> listOrders();
    void updateStatus(String orderId, OrderStatus status);
    void delete(String orderId);
    BigDecimal calculateTotal(String orderId);
}
