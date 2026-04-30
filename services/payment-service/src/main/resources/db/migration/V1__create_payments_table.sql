-- Payment Service - V1 Migration
-- Ödeme tabloları oluşturma

CREATE TABLE IF NOT EXISTS payments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_reference     UUID NOT NULL UNIQUE,
    user_id             UUID NOT NULL,
    amount              NUMERIC(10, 2) NOT NULL,
    currency            VARCHAR(10) NOT NULL DEFAULT 'TRY',
    status              VARCHAR(20) NOT NULL,
    -- PENDING | COMPLETED | FAILED
    iyzico_payment_id   VARCHAR(255),
    iyzico_response     JSONB,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id        VARCHAR(255) PRIMARY KEY,
    processed_at    TIMESTAMP NOT NULL DEFAULT now()
);

-- Indexler
CREATE INDEX IF NOT EXISTS idx_payments_order_reference ON payments(order_reference);
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
