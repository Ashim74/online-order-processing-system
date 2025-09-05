package com.example.orders.service;

import com.example.orders.dao.OrderDao;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.model.Product;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DefaultOrderService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(DefaultOrderService.class);

    private final OrderDao orderDao;
    private final Validator validator;

    public DefaultOrderService(OrderDao orderDao, Validator validator) {
        this.orderDao = orderDao;
        this.validator = validator;
    }

    @Override
    @Transactional
    public void placeOrder(Order order) {
        var violations = validator.validate(order.getCustomer());
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Invalid customer email");
        }
        orderDao.save(order);
        log.info("Placed order {}", order.getOrderId());
    }

    @Override
    public Order getOrder(String orderId) {
        return orderDao.findById(orderId);
    }

    @Override
    public List<Order> listOrders() {
        return orderDao.findAll();
    }

    @Override
    @Transactional
    public void updateStatus(String orderId, OrderStatus status) {
        Order order = orderDao.findById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderDao.save(order);
            log.info("Updated status of order {} to {}", orderId, status);
        }
    }

    @Override
    @Transactional
    public void delete(String orderId) {
        orderDao.deleteById(orderId);
        log.info("Deleted order {}", orderId);
    }

    @Override
    public BigDecimal calculateTotal(String orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return BigDecimal.ZERO;
        }
        return order.getProducts().stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
