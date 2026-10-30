CREATE TABLE IF NOT EXISTS sellers (
  seller_id BIGINT PRIMARY KEY,
  seller_name VARCHAR(120) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','SELLER'))
);
CREATE TABLE IF NOT EXISTS products (
  product_id BIGSERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  category VARCHAR(30) NOT NULL CHECK (category IN ('ELECTRONICS','FASHION','HOME','BOOKS','GROCERY')),
  seller_id BIGINT NOT NULL REFERENCES sellers(seller_id),
  price NUMERIC(12,2) NOT NULL CHECK (price > 0),
  stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Minimal order reference tables for testing Module 1 restrictions.
CREATE TABLE IF NOT EXISTS orders (
  order_id BIGSERIAL PRIMARY KEY,
  status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED','COMPLETED'))
);
CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(order_id),
  product_id BIGINT NOT NULL REFERENCES products(product_id),
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price > 0)
);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);
