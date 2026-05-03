-- Payment Service - V2 Migration
-- Iyzico Checkout Form entegrasyonu için yeni kolonlar

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS checkout_form_url TEXT,
    ADD COLUMN IF NOT EXISTS iyzico_token      VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_payments_iyzico_token ON payments(iyzico_token);
