package com.example.order.model;

import java.util.regex.Pattern;

public class Customer {
    private int customerId;
    private String name;
    private String email;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[A-Za-z]{2,6}$");

    public Customer() {}

    public Customer(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        setEmail(email);
    }

    public Customer(String name, String email) {
        this.name = name;
        setEmail(email);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
