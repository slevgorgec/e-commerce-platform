-- Order Service - V1 Migration
-- Sipariş tabloları oluşturma

CREATE TABLE IF NOT EXISTS orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    status          VARCHAR(30) NOT NULL,
    -- PENDING | STOCK_RESERVED | PAYMENT_REQUESTED | CONFIRMED | CANCELLED
    total_amount    NUMERIC(10, 2) NOT NULL,
    shipping_address JSONB NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                UUID NOT NULL REFERENCES orders(id),
    product_id              UUID NOT NULL,
    variant_id              UUID NOT NULL,
    product_name_snapshot   VARCHAR(255) NOT NULL,
    variant_value_snapshot  VARCHAR(100) NOT NULL,
    unit_price_snapshot     NUMERIC(10, 2) NOT NULL,
    quantity                INTEGER NOT NULL,
    CONSTRAINT chk_quantity CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS order_status_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID NOT NULL REFERENCES orders(id),
    from_status VARCHAR(30),
    to_status   VARCHAR(30) NOT NULL,
    changed_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id        VARCHAR(255) PRIMARY KEY,
    processed_at    TIMESTAMP NOT NULL DEFAULT now()
);

-- Indexler
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_status_history_order_id ON order_status_history(order_id);