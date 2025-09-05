# Online Order Processing System

A simple Java and Spring-based application for managing customer orders. The application demonstrates placing an order, viewing order details, updating the order status, calculating the order total, and deleting the order.

## Requirements
- Java 17+
- Maven
- PostgreSQL database running on `localhost:5432` with database `ordersdb`, user `postgres`, and password `password`.

## Running the Application
1. Ensure PostgreSQL is installed and running. Create the database:
   ```sql
   CREATE DATABASE ordersdb;
   ```
2. Build the project:
   ```bash
   mvn package
   ```
3. Run the main demonstration:
   ```bash
   java -cp target/online-order-processing-system-1.0-SNAPSHOT.jar com.example.order.MainApp
   ```

Logging output will show the operations being performed. The program creates necessary tables automatically on first run.
