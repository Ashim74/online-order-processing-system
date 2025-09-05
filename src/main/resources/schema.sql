CREATE TABLE IF NOT EXISTS customers (
  customer_id VARCHAR(50) PRIMARY KEY,
  name        VARCHAR(200) NOT NULL,
  email       VARCHAR(320) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  product_id  VARCHAR(50) PRIMARY KEY,
  name        VARCHAR(200) NOT NULL,
  price       NUMERIC(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
  order_id    VARCHAR(50) PRIMARY KEY,
  customer_id VARCHAR(50) NOT NULL REFERENCES customers(customer_id),
  status      VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  order_id   VARCHAR(50) NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
  product_id VARCHAR(50) NOT NULL REFERENCES products(product_id),
  PRIMARY KEY (order_id, product_id)
);
