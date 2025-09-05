package com.example.order.dao;

import com.example.order.model.Customer;
import com.example.order.model.Order;
import com.example.order.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {

    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        init();
    }

    private void init() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers (customer_id SERIAL PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (product_id SERIAL PRIMARY KEY, name VARCHAR(255), price NUMERIC)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (order_id SERIAL PRIMARY KEY, customer_id INT REFERENCES customers(customer_id), status VARCHAR(50))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS order_products (order_id INT REFERENCES orders(order_id), product_id INT REFERENCES products(product_id))");
    }

    @Override
    public Order placeOrder(Order order) {
        logger.info("Placing order for {}", order.getCustomer().getEmail());
        Integer customerId = jdbcTemplate.queryForObject(
                "INSERT INTO customers(name, email) VALUES (?, ?) RETURNING customer_id",
                new Object[]{order.getCustomer().getName(), order.getCustomer().getEmail()}, Integer.class);
        order.getCustomer().setCustomerId(customerId);

        List<Integer> productIds = new ArrayList<>();
        for (Product p : order.getProducts()) {
            Integer productId = jdbcTemplate.queryForObject(
                    "INSERT INTO products(name, price) VALUES (?, ?) RETURNING product_id",
                    new Object[]{p.getName(), p.getPrice()}, Integer.class);
            p.setProductId(productId);
            productIds.add(productId);
        }

        Integer orderId = jdbcTemplate.queryForObject(
                "INSERT INTO orders(customer_id, status) VALUES (?, ?) RETURNING order_id",
                new Object[]{customerId, order.getStatus()}, Integer.class);
        order.setOrderId(orderId);

        for (Integer pid : productIds) {
            jdbcTemplate.update("INSERT INTO order_products(order_id, product_id) VALUES (?, ?)", orderId, pid);
        }
        return order;
    }

    @Override
    public Order getOrder(int orderId) {
        logger.info("Fetching order {}", orderId);
        Order order = jdbcTemplate.queryForObject(
                "SELECT o.order_id, o.status, c.customer_id, c.name, c.email " +
                        "FROM orders o JOIN customers c ON o.customer_id = c.customer_id WHERE o.order_id = ?",
                new OrderRowMapper(), orderId);
        List<Product> products = jdbcTemplate.query(
                "SELECT p.product_id, p.name, p.price FROM products p JOIN order_products op ON p.product_id = op.product_id WHERE op.order_id = ?",
                new ProductRowMapper(), orderId);
        order.setProducts(products);
        return order;
    }

    @Override
    public void updateOrderStatus(int orderId, String status) {
        logger.info("Updating order {} to status {}", orderId, status);
        jdbcTemplate.update("UPDATE orders SET status = ? WHERE order_id = ?", status, orderId);
    }

    @Override
    public void deleteOrder(int orderId) {
        logger.info("Deleting order {}", orderId);
        jdbcTemplate.update("DELETE FROM order_products WHERE order_id = ?", orderId);
        jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId);
    }

    @Override
    public double calculateTotalValue(int orderId) {
        logger.info("Calculating total value for order {}", orderId);
        Double total = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(p.price),0) FROM products p JOIN order_products op ON p.product_id = op.product_id WHERE op.order_id = ?",
                Double.class, orderId);
        return total != null ? total : 0.0;
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("email"));
            Order order = new Order();
            order.setOrderId(rs.getInt("order_id"));
            order.setStatus(rs.getString("status"));
            order.setCustomer(customer);
            return order;
        }
    }

    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Product(rs.getInt("product_id"), rs.getString("name"), rs.getDouble("price"));
        }
    }
}
