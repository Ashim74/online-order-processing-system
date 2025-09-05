package com.example.orders.app;

import com.example.orders.config.AppConfig;
import com.example.orders.model.*;
import com.example.orders.service.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService service = ctx.getBean(OrderService.class);

        Customer customer = new Customer("C1", "John Doe", "john@example.com");
        Product p1 = new Product("P1", "Widget", new BigDecimal("10.00"));
        Product p2 = new Product("P2", "Gadget", new BigDecimal("15.50"));
        Order order = new Order("O1", customer, Arrays.asList(p1, p2), OrderStatus.PENDING);

        service.placeOrder(order);
        Order fetched = service.getOrder("O1");
        System.out.println("Fetched order: " + fetched.getOrderId());
        BigDecimal total = service.calculateTotal("O1");
        System.out.println("Total: " + total);
        service.updateStatus("O1", OrderStatus.SHIPPED);
        service.delete("O1");

        ctx.close();
    }
}
