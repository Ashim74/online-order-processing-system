package com.example.orders.dao;

import com.example.orders.model.Customer;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcOrderDao implements OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        createSchema();
    }

    private void createSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers (\n" +
                "  customer_id VARCHAR(50) PRIMARY KEY,\n" +
                "  name        VARCHAR(200) NOT NULL,\n" +
                "  email       VARCHAR(320) NOT NULL\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (\n" +
                "  product_id  VARCHAR(50) PRIMARY KEY,\n" +
                "  name        VARCHAR(200) NOT NULL,\n" +
                "  price       NUMERIC(12,2) NOT NULL\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (\n" +
                "  order_id    VARCHAR(50) PRIMARY KEY,\n" +
                "  customer_id VARCHAR(50) NOT NULL REFERENCES customers(customer_id),\n" +
                "  status      VARCHAR(20) NOT NULL\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS order_items (\n" +
                "  order_id   VARCHAR(50) NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,\n" +
                "  product_id VARCHAR(50) NOT NULL REFERENCES products(product_id),\n" +
                "  PRIMARY KEY (order_id, product_id)\n" +
                ")");
    }

    @Override
    public void save(Order order) {
        Customer customer = order.getCustomer();
        jdbcTemplate.update("INSERT INTO customers(customer_id, name, email) VALUES (?,?,?) ON CONFLICT (customer_id) DO UPDATE SET name = EXCLUDED.name, email = EXCLUDED.email",
                customer.getCustomerId(), customer.getName(), customer.getEmail());

        for (Product p : order.getProducts()) {
            jdbcTemplate.update("INSERT INTO products(product_id, name, price) VALUES (?,?,?) ON CONFLICT (product_id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price",
                    p.getProductId(), p.getName(), p.getPrice());
        }

        jdbcTemplate.update("INSERT INTO orders(order_id, customer_id, status) VALUES (?,?,?) ON CONFLICT (order_id) DO UPDATE SET customer_id = EXCLUDED.customer_id, status = EXCLUDED.status",
                order.getOrderId(), customer.getCustomerId(), order.getStatus().name());

        jdbcTemplate.update("DELETE FROM order_items WHERE order_id = ?", order.getOrderId());
        for (Product p : order.getProducts()) {
            jdbcTemplate.update("INSERT INTO order_items(order_id, product_id) VALUES (?,?)", order.getOrderId(), p.getProductId());
        }
    }

    @Override
    public Order findById(String orderId) {
        String sql = "SELECT order_id, customer_id, status FROM orders WHERE order_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }
            String customerId = rs.getString("customer_id");
            OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
            Customer customer = jdbcTemplate.queryForObject(
                    "SELECT customer_id, name, email FROM customers WHERE customer_id = ?",
                    new CustomerRowMapper(), customerId);
            List<Product> products = jdbcTemplate.query(
                    "SELECT p.product_id, p.name, p.price FROM products p JOIN order_items oi ON p.product_id = oi.product_id WHERE oi.order_id = ?",
                    new ProductRowMapper(), orderId);
            return new Order(orderId, customer, products, status);
        }, orderId);
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT order_id FROM orders";
        List<String> ids = jdbcTemplate.query(sql, (rs, i) -> rs.getString("order_id"));
        List<Order> orders = new ArrayList<>();
        for (String id : ids) {
            Order order = findById(id);
            if (order != null) {
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void deleteById(String orderId) {
        jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId);
    }

    static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(rs.getString("customer_id"), rs.getString("name"), rs.getString("email"));
        }
    }

    static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Product(rs.getString("product_id"), rs.getString("name"), rs.getBigDecimal("price"));
        }
    }
}
