-- Orders table — one record per order
CREATE TABLE orders (
    id                  BIGSERIAL PRIMARY KEY,
    buyer_id            BIGINT NOT NULL REFERENCES users(id),
    -- who placed the order

    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED

    total_amount        DECIMAL(10,2) NOT NULL,
    -- total at time of order

    -- Delivery address captured at order time
    -- not linked to a separate address table yet
    -- because address may change later
    delivery_name       VARCHAR(100) NOT NULL,
    delivery_phone      VARCHAR(20) NOT NULL,
    delivery_address    VARCHAR(255) NOT NULL,
    delivery_city       VARCHAR(100) NOT NULL,
    delivery_state      VARCHAR(100) NOT NULL,
    delivery_pincode    VARCHAR(10) NOT NULL,

    payment_status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING → PAID → FAILED → REFUNDED

    payment_method      VARCHAR(20),
    -- COD, UPI, CARD, NETBANKING

    notes               TEXT,
    -- buyer's special instructions

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Order items table — one record per product in order
CREATE TABLE order_items (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT NOT NULL REFERENCES orders(id),
    variant_id          BIGINT NOT NULL REFERENCES product_variants(id),

    product_name        VARCHAR(255) NOT NULL,
    -- captured at order time — product name may change later

    variant_name        VARCHAR(100) NOT NULL,
    -- "30 Tablets" — captured at order time

    price_at_order      DECIMAL(10,2) NOT NULL,
    -- price when buyer ordered — never changes

    quantity            INTEGER NOT NULL,

    subtotal            DECIMAL(10,2) NOT NULL,
    -- price_at_order × quantity

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_variant ON order_items(variant_id);