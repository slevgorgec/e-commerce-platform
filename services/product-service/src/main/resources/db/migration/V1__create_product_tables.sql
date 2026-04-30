CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE categories (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(100) NOT NULL,
    slug      VARCHAR(100) UNIQUE NOT NULL,
    parent_id UUID REFERENCES categories(id)
);

CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent ON categories(parent_id);

CREATE TABLE products (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    category_id UUID REFERENCES categories(id),
    base_price  NUMERIC(10,2) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP
);

CREATE INDEX idx_products_slug       ON products(slug);
CREATE INDEX idx_products_category   ON products(category_id);
CREATE INDEX idx_products_active     ON products(active);

CREATE TABLE product_variants (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id     UUID NOT NULL REFERENCES products(id),
    sku            VARCHAR(100) UNIQUE NOT NULL,
    variant_value  VARCHAR(100) NOT NULL,
    price          NUMERIC(10,2) NOT NULL,
    stock          INTEGER NOT NULL DEFAULT 0,
    reserved_stock INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT chk_stock    CHECK (stock >= 0),
    CONSTRAINT chk_reserved CHECK (reserved_stock >= 0),
    CONSTRAINT chk_available CHECK (stock >= reserved_stock)
);

CREATE INDEX idx_variants_product ON product_variants(product_id);
CREATE INDEX idx_variants_sku     ON product_variants(sku);

CREATE TABLE stock_reservations (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID NOT NULL,
    variant_id UUID NOT NULL REFERENCES product_variants(id),
    quantity   INTEGER NOT NULL,
    status     VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservations_order   ON stock_reservations(order_id);
CREATE INDEX idx_reservations_variant ON stock_reservations(variant_id);
CREATE INDEX idx_reservations_status  ON stock_reservations(status);

CREATE TABLE processed_events (
    event_id     VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT now()
);
