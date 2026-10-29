-- Main product table
CREATE TABLE products (
    id                  BIGSERIAL PRIMARY KEY,
    seller_id           BIGINT NOT NULL REFERENCES users(id),
    name                VARCHAR(255) NOT NULL,
    slug                VARCHAR(255) NOT NULL UNIQUE,
    -- slug = URL friendly name
    -- "Zanskar Joint Support" → "zanskar-joint-support"
    -- used in URLs like /products/zanskar-joint-support
    description         TEXT,
    category            VARCHAR(100) NOT NULL,
    subcategory         VARCHAR(100),
    brand               VARCHAR(100),
    tags                TEXT[],
    -- array of search tags ["joint", "supplement", "glucosamine"]
    weight              VARCHAR(50),
    dimensions          VARCHAR(100),
    is_returnable       BOOLEAN NOT NULL DEFAULT TRUE,
    warranty_months     INTEGER DEFAULT 0,
    average_rating      DECIMAL(3,2) DEFAULT 0.00,
    rating_count        INTEGER DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    -- DRAFT → PENDING_APPROVAL → ACTIVE → DISCONTINUED
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product variants table
-- One product can have many variants (30/60/90 tablets)
CREATE TABLE product_variants (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    variant_name    VARCHAR(100) NOT NULL,
    -- "30 Tablets", "60 Tablets", "90 Tablets"
    sku             VARCHAR(100) NOT NULL UNIQUE,
    -- unique code for each variant — for inventory tracking
    price           DECIMAL(10,2) NOT NULL,
    mrp             DECIMAL(10,2),
    -- mrp = maximum retail price
    -- discount = ((mrp - price) / mrp) * 100 — calculated, never stored
    stock_quantity  INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER NOT NULL DEFAULT 10,
    -- alert seller when stock goes below this number
    attributes      JSONB,
    -- flexible key-value for variant specific attributes
    -- tablets: {"flavour": "unflavoured", "count": "30"}
    -- shoes: {"size": "42", "color": "black"}
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product images table
-- Separate table for ordered images with alt text
CREATE TABLE product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products(id),
    image_url   VARCHAR(500) NOT NULL,
    alt_text    VARCHAR(255),
    -- alt text = description of image for SEO and accessibility
    display_order INTEGER NOT NULL DEFAULT 0,
    -- controls which image shows first, second, etc.
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for fast queries
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_variants_product ON product_variants(product_id);
CREATE INDEX idx_variants_sku ON product_variants(sku);
CREATE INDEX idx_images_product ON product_images(product_id);