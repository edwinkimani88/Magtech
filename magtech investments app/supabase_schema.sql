-- ============================================================
-- MAGTECH INVESTMENTS SUPABASE DATABASE SCHEMA
-- Execute this SQL directly in the Supabase SQL Editor
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. SHOPS TABLE
CREATE TABLE IF NOT EXISTS shops (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL,
    phone TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed two shops in Kitengela
INSERT INTO shops (id, name, location, phone) VALUES
('shop_1', 'MagTech Shop 1 - Kitengela Chairman Rd', 'Chairman Road, Kitengela', '+254712345678'),
('shop_2', 'MagTech Shop 2 - Kitengela Deliverance Rd', 'Deliverance Road, Kitengela', '+254787654321')
ON CONFLICT (id) DO NOTHING;

-- 2. PROFILES / ADMINS TABLE
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    shop_id TEXT REFERENCES shops(id) ON DELETE SET NULL,
    role TEXT NOT NULL DEFAULT 'admin',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed default admin profiles
INSERT INTO profiles (id, email, full_name, shop_id, role) VALUES
('11111111-1111-1111-1111-111111111111', 'admin1@magtech.co.ke', 'Admin (Chairman Rd)', 'shop_1', 'admin'),
('22222222-2222-2222-2222-222222222222', 'admin2@magtech.co.ke', 'Admin (Deliverance Rd)', 'shop_2', 'admin')
ON CONFLICT (email) DO NOTHING;

-- 3. CUSTOMERS TABLE
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    id_number TEXT NOT NULL,
    phone TEXT NOT NULL,
    shop_id TEXT REFERENCES shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. LOANS TABLE
CREATE TABLE IF NOT EXISTS loans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_number TEXT UNIQUE NOT NULL,
    customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
    shop_id TEXT REFERENCES shops(id),
    loan_amount NUMERIC(12,2) NOT NULL,
    amount_payable NUMERIC(12,2) NOT NULL,
    total_paid NUMERIC(12,2) DEFAULT 0.00,
    balance_payable NUMERIC(12,2) NOT NULL,
    start_date DATE DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PARTIALLY_PAID, PAID, EXTENDED, OVERDUE, DEFAULTED, CLOSED
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. LOAN ITEMS (COLLATERAL) TABLE
CREATE TABLE IF NOT EXISTS loan_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id UUID REFERENCES loans(id) ON DELETE CASCADE,
    category TEXT NOT NULL, -- Phones, Laptops, TVs, Audio, Fridges, Cookers, Home Appliances, Kitchen, Gaming, Accessories, Other
    item_name TEXT NOT NULL,
    description TEXT,
    condition TEXT NOT NULL, -- LIKE NEW, GOOD, FAIR
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. LOAN PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS loan_payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id UUID REFERENCES loans(id) ON DELETE CASCADE,
    shop_id TEXT REFERENCES shops(id),
    amount NUMERIC(12,2) NOT NULL,
    payment_date TIMESTAMPTZ DEFAULT NOW(),
    payment_method TEXT DEFAULT 'M-PESA',
    previous_balance NUMERIC(12,2) NOT NULL,
    new_balance NUMERIC(12,2) NOT NULL,
    received_by TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. LOAN RENEWALS TABLE
CREATE TABLE IF NOT EXISTS loan_renewals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id UUID REFERENCES loans(id) ON DELETE CASCADE,
    shop_id TEXT REFERENCES shops(id),
    renewal_number INT NOT NULL DEFAULT 1,
    fee_paid NUMERIC(12,2) NOT NULL,
    old_due_date DATE NOT NULL,
    new_due_date DATE NOT NULL,
    renewal_date TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. PRODUCTS (INVENTORY) TABLE
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    condition TEXT NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    source TEXT NOT NULL, -- LOAN, PURCHASE, CONSIGNMENT, DIRECT
    source_id UUID,
    shop_id TEXT REFERENCES shops(id),
    status TEXT NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, RESERVED, SOLD, RETURNED
    description TEXT,
    is_marketplace_visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. PRODUCT IMAGES TABLE
CREATE TABLE IF NOT EXISTS product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID REFERENCES products(id) ON DELETE CASCADE,
    loan_id UUID REFERENCES loans(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. PURCHASES TABLE
CREATE TABLE IF NOT EXISTS purchases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_name TEXT NOT NULL,
    category TEXT NOT NULL,
    purchase_amount NUMERIC(12,2) NOT NULL,
    seller_name TEXT NOT NULL,
    seller_phone TEXT NOT NULL,
    seller_id_number TEXT,
    shop_id TEXT REFERENCES shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. CONSIGNMENTS TABLE
CREATE TABLE IF NOT EXISTS consignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_name TEXT NOT NULL,
    category TEXT NOT NULL,
    agreed_price NUMERIC(12,2) NOT NULL,
    commission_rate NUMERIC(5,2) DEFAULT 10.00,
    owner_name TEXT NOT NULL,
    owner_phone TEXT NOT NULL,
    shop_id TEXT REFERENCES shops(id),
    status TEXT DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 12. SALES TABLE
CREATE TABLE IF NOT EXISTS sales (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID REFERENCES products(id) ON DELETE SET NULL,
    sale_amount NUMERIC(12,2) NOT NULL,
    buyer_name TEXT,
    buyer_phone TEXT,
    payment_method TEXT DEFAULT 'M-PESA',
    shop_id TEXT REFERENCES shops(id),
    sold_by TEXT,
    sale_date TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 13. TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type TEXT NOT NULL, -- LOAN_CREATED, PAYMENT_RECEIVED, RENEWAL_EXTENDED, PRODUCT_PURCHASED, PRODUCT_SOLD, CONSIGNMENT_RECEIVED
    title TEXT NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    shop_id TEXT REFERENCES shops(id),
    reference_id UUID,
    reference_type TEXT,
    details_json JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 14. AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    action TEXT NOT NULL,
    entity_type TEXT NOT NULL,
    entity_id UUID,
    shop_id TEXT REFERENCES shops(id),
    performed_by TEXT,
    details TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- INDEXES FOR LOW LATENCY PERFORMANCE
CREATE INDEX IF NOT EXISTS idx_loans_shop_id ON loans(shop_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_products_shop_id ON products(shop_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_marketplace ON products(is_marketplace_visible);
CREATE INDEX IF NOT EXISTS idx_transactions_shop_id ON transactions(shop_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at DESC);

-- ROW LEVEL SECURITY (RLS)
ALTER TABLE shops ENABLE ROW LEVEL SECURITY;
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE loans ENABLE ROW LEVEL SECURITY;
ALTER TABLE loan_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE loan_payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE loan_renewals ENABLE ROW LEVEL SECURITY;
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE product_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE purchases ENABLE ROW LEVEL SECURITY;
ALTER TABLE consignments ENABLE ROW LEVEL SECURITY;
ALTER TABLE sales ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

-- PUBLIC READ ACCESS FOR MARKETPLACE PRODUCTS
CREATE POLICY "Public marketplace products view" ON products
    FOR SELECT USING (is_marketplace_visible = true AND status = 'AVAILABLE');

CREATE POLICY "Public product images view" ON product_images
    FOR SELECT USING (true);

-- FULL ACCESS FOR AUTHENTICATED USERS / ADMINS
CREATE POLICY "Admin full access shops" ON shops FOR ALL USING (true);
CREATE POLICY "Admin full access profiles" ON profiles FOR ALL USING (true);
CREATE POLICY "Admin full access customers" ON customers FOR ALL USING (true);
CREATE POLICY "Admin full access loans" ON loans FOR ALL USING (true);
CREATE POLICY "Admin full access loan_items" ON loan_items FOR ALL USING (true);
CREATE POLICY "Admin full access loan_payments" ON loan_payments FOR ALL USING (true);
CREATE POLICY "Admin full access loan_renewals" ON loan_renewals FOR ALL USING (true);
CREATE POLICY "Admin full access products" ON products FOR ALL USING (true);
CREATE POLICY "Admin full access product_images" ON product_images FOR ALL USING (true);
CREATE POLICY "Admin full access purchases" ON purchases FOR ALL USING (true);
CREATE POLICY "Admin full access consignments" ON consignments FOR ALL USING (true);
CREATE POLICY "Admin full access sales" ON sales FOR ALL USING (true);
CREATE POLICY "Admin full access transactions" ON transactions FOR ALL USING (true);
CREATE POLICY "Admin full access audit_logs" ON audit_logs FOR ALL USING (true);

-- STORAGE BUCKETS
INSERT INTO storage.buckets (id, name, public) VALUES ('magtech-photos', 'magtech-photos', true)
ON CONFLICT (id) DO NOTHING;

CREATE POLICY "Public access to magtech-photos" ON storage.objects
    FOR SELECT USING (bucket_id = 'magtech-photos');

CREATE POLICY "Upload access to magtech-photos" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'magtech-photos');
