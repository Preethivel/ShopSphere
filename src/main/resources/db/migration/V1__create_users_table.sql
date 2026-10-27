-- Version 1 — Users table
-- This is the first and foundational table of ShopSphere.
-- All three roles (BUYER, SELLER, ADMIN) are stored here.
-- Role-based access is handled at the application layer.

CREATE TABLE users (
    -- Auto-incrementing unique ID for every user
    id BIGSERIAL PRIMARY KEY,

    full_name VARCHAR(100) NOT NULL,

    -- Used for login. UNIQUE enforced at DB level, not just app level.
    email VARCHAR(255) NOT NULL UNIQUE,

    -- bcrypt hash stored here, never plain text password
    password_hash VARCHAR(255) NOT NULL,

    -- One of: BUYER, SELLER, ADMIN
    role VARCHAR(20) NOT NULL DEFAULT 'BUYER',

    -- Soft delete — deactivated accounts stay in DB
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Audit trail — required in any production system
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Login queries always search by email — this index makes it instant
CREATE INDEX idx_users_email ON users(email);

-- Admin dashboard filters by role constantly
CREATE INDEX idx_users_role ON users(role);