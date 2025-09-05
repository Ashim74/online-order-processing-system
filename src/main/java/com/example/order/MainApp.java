package com.example.order;

import com.example.order.config.AppConfig;
import com.example.order.dao.OrderDAO;
import com.example.order.model.Customer;
import com.example.order.model.Order;
import com.example.order.model.Product;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderDAO orderDAO = context.getBean(OrderDAO.class);

        Customer customer = new Customer("John Doe", "john.doe@example.com");
        Product p1 = new Product("Book", 19.99);
        Product p2 = new Product("Pen", 2.49);
        Order order = new Order(customer, Arrays.asList(p1, p2), "Pending");

        Order placed = orderDAO.placeOrder(order);
        System.out.println("Placed Order: " + placed);

        Order fetched = orderDAO.getOrder(placed.getOrderId());
        System.out.println("Fetched Order: " + fetched);

        orderDAO.updateOrderStatus(placed.getOrderId(), "Shipped");
        Order updated = orderDAO.getOrder(placed.getOrderId());
        System.out.println("Updated Order: " + updated);

        double total = orderDAO.calculateTotalValue(placed.getOrderId());
        System.out.println("Total Order Value: " + total);

        orderDAO.deleteOrder(placed.getOrderId());
        System.out.println("Order deleted.");
    }
}
