-- Update status column to support automated approval flow
-- Old flow: DRAFT → PENDING_APPROVAL → ACTIVE → DISCONTINUED
-- New flow: DRAFT → PENDING_REVIEW → ACTIVE → REJECTED → SUSPENDED → DISCONTINUED

ALTER TABLE products 
DROP COLUMN status;
-- remove old status column

ALTER TABLE products 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
-- add it back with same definition
-- existing data defaults to DRAFT

ALTER TABLE products
ADD COLUMN rejection_reason TEXT;
-- why was product rejected — shown to seller
-- null when active or draft

ALTER TABLE products
ADD COLUMN reviewed_at TIMESTAMP;
-- when automated review completed