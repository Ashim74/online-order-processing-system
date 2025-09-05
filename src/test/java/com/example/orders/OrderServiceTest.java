package com.example.orders;

import com.example.orders.dao.OrderDao;
import com.example.orders.model.*;
import com.example.orders.service.DefaultOrderService;
import com.example.orders.service.OrderService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void calculateTotal() {
        OrderDao dao = new OrderDao() {
            @Override
            public void save(Order order) { }
            @Override
            public Order findById(String orderId) {
                Customer c = new Customer("C1", "Alice", "alice@example.com");
                List<Product> products = Arrays.asList(
                        new Product("P1", "Widget", new BigDecimal("5.00")),
                        new Product("P2", "Gadget", new BigDecimal("10.00"))
                );
                return new Order(orderId, c, products, OrderStatus.PENDING);
            }
            @Override
            public List<Order> findAll() { return Collections.emptyList(); }
            @Override
            public void deleteById(String orderId) { }
        };
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        OrderService service = new DefaultOrderService(dao, validator);
        BigDecimal total = service.calculateTotal("1");
        assertEquals(new BigDecimal("15.00"), total);
    }
}
