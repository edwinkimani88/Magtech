-- ====================================================================
-- MAGTECH INVESTMENTS - SUPABASE DATABASE SCHEMA & MIGRATION SCRIPT
-- Executable directly in Supabase SQL Editor
-- ====================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. SHOPS TABLE
CREATE TABLE IF NOT EXISTS public.shops (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL,
    phone_number TEXT DEFAULT '+254 700 000 000',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed Shops
INSERT INTO public.shops (id, name, location, phone_number)
VALUES 
    ('Shop 1', 'MagTech Chairman Road Branch', 'Chairman Road, Nairobi', '+254 712 345 678'),
    ('Shop 2', 'MagTech Deliverance Road Branch', 'Deliverance Road, Nairobi', '+254 723 456 789')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name,
    location = EXCLUDED.location;

-- 2. ADMIN PROFILES
CREATE TABLE IF NOT EXISTS public.admins (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    shop_id TEXT REFERENCES public.shops(id),
    role TEXT NOT NULL DEFAULT 'ADMIN', -- 'ADMIN'
    pin_hash TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed Default Admins
INSERT INTO public.admins (email, full_name, shop_id, role)
VALUES
    ('admin1@magtech.co.ke', 'Admin Shop 1 (Chairman Road)', 'Shop 1', 'ADMIN'),
    ('admin2@magtech.co.ke', 'Admin Shop 2 (Deliverance Road)', 'Shop 2', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- 3. CUSTOMERS TABLE
CREATE TABLE IF NOT EXISTS public.customers (
    id BIGSERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    national_id TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    notes TEXT DEFAULT '',
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_customers_phone ON public.customers(phone_number);
CREATE INDEX IF NOT EXISTS idx_customers_national_id ON public.customers(national_id);

-- 4. ITEMS / PRODUCTS TABLE (Shared for Loans, Direct Buy-Ins & Marketplace)
CREATE TABLE IF NOT EXISTS public.items (
    id BIGSERIAL PRIMARY KEY,
    item_name TEXT NOT NULL,
    category TEXT NOT NULL CHECK (category IN (
        'Phones', 'Laptops', 'TVs', 'Audio', 'Fridges', 'Cookers', 
        'Home Appliances', 'Kitchen', 'Gaming', 'Accessories', 'Other'
    )),
    brand TEXT NOT NULL,
    condition TEXT NOT NULL CHECK (condition IN ('LIKE NEW', 'GOOD', 'FAIR')),
    estimated_market_value NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    forced_sale_value NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    notes TEXT DEFAULT '',
    photo_urls_json TEXT DEFAULT '',
    status TEXT NOT NULL DEFAULT 'ACTIVE_LOAN' CHECK (status IN (
        'Active Loan', 'Redeemed', 'Purchased', 'Listed', 'Sold', 'Disposed', 'Forfeited'
    )),
    entry_type TEXT NOT NULL CHECK (entry_type IN ('LOAN', 'DIRECT_PURCHASE', 'CONSIGNMENT')),
    is_published_to_marketplace BOOLEAN DEFAULT TRUE,
    marketplace_price NUMERIC(12,2) DEFAULT 0.00,
    customer_id BIGINT REFERENCES public.customers(id) ON DELETE SET NULL,
    shop_location TEXT NOT NULL DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_items_status ON public.items(status);
CREATE INDEX IF NOT EXISTS idx_items_shop ON public.items(shop_location);
CREATE INDEX IF NOT EXISTS idx_items_marketplace ON public.items(is_published_to_marketplace) WHERE is_published_to_marketplace = TRUE;

-- 5. PRODUCT IMAGES TABLE
CREATE TABLE IF NOT EXISTS public.product_images (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES public.items(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. LOANS TABLE
CREATE TABLE IF NOT EXISTS public.loans (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES public.items(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES public.customers(id) ON DELETE CASCADE,
    amount_given NUMERIC(12,2) NOT NULL,
    total_payable NUMERIC(12,2) NOT NULL,
    paid_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    balance_payable NUMERIC(12,2) GENERATED ALWAYS AS (total_payable - paid_amount) STORED,
    date_issued TIMESTAMPTZ DEFAULT NOW(),
    due_date TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN (
        'ACTIVE', 'PARTIALLY_PAID', 'PAID', 'EXTENDED', 'OVERDUE', 'DEFAULTED', 'CLOSED'
    )),
    renewal_count INT DEFAULT 0,
    shop_location TEXT NOT NULL DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_loans_status ON public.loans(status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON public.loans(due_date);

-- 7. LOAN PAYMENTS (Partial Repayments History)
CREATE TABLE IF NOT EXISTS public.loan_payments (
    id BIGSERIAL PRIMARY KEY,
    loan_id BIGINT NOT NULL REFERENCES public.loans(id) ON DELETE CASCADE,
    payment_amount NUMERIC(12,2) NOT NULL,
    previous_balance NUMERIC(12,2) NOT NULL,
    new_balance NUMERIC(12,2) NOT NULL,
    payment_method TEXT DEFAULT 'CASH', -- M-PESA, CASH
    admin_user TEXT DEFAULT 'Admin',
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. LOAN RENEWALS / EXTENSIONS
CREATE TABLE IF NOT EXISTS public.loan_renewals (
    id BIGSERIAL PRIMARY KEY,
    loan_id BIGINT NOT NULL REFERENCES public.loans(id) ON DELETE CASCADE,
    renewal_fee NUMERIC(12,2) NOT NULL,
    previous_due_date TIMESTAMPTZ NOT NULL,
    new_due_date TIMESTAMPTZ NOT NULL,
    renewal_number INT NOT NULL,
    admin_user TEXT DEFAULT 'Admin',
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. PURCHASES TABLE (Direct Buy-ins)
CREATE TABLE IF NOT EXISTS public.purchases (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES public.items(id) ON DELETE CASCADE,
    seller_name TEXT,
    seller_phone TEXT,
    purchase_price NUMERIC(12,2) NOT NULL,
    admin_user TEXT DEFAULT 'Admin',
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. SALES TABLE (Marketplace / Counter Sales)
CREATE TABLE IF NOT EXISTS public.sales (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES public.items(id) ON DELETE CASCADE,
    sale_price NUMERIC(12,2) NOT NULL,
    buyer_name TEXT,
    buyer_phone TEXT,
    admin_user TEXT DEFAULT 'Admin',
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. TRANSACTIONS / AUDIT LOGS
CREATE TABLE IF NOT EXISTS public.transactions (
    id BIGSERIAL PRIMARY KEY,
    type TEXT NOT NULL, -- 'LOAN_DISBURSED', 'LOAN_REPAYMENT', 'LOAN_EXTENSION', 'DIRECT_PURCHASE', 'MARKETPLACE_SALE'
    amount NUMERIC(12,2) NOT NULL,
    item_id BIGINT REFERENCES public.items(id) ON DELETE SET NULL,
    customer_id BIGINT REFERENCES public.customers(id) ON DELETE SET NULL,
    description TEXT NOT NULL,
    shop_location TEXT DEFAULT 'Shop 1' REFERENCES public.shops(id),
    admin_user TEXT DEFAULT 'Admin',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tx_created ON public.transactions(created_at DESC);

-- 12. SMS LOGS
CREATE TABLE IF NOT EXISTS public.sms_logs (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES public.customers(id) ON DELETE SET NULL,
    phone_number TEXT NOT NULL,
    message_text TEXT NOT NULL,
    status TEXT DEFAULT 'SENT',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ====================================================================
-- STORAGE BUCKETS SETUP
-- ====================================================================
INSERT INTO storage.buckets (id, name, public) 
VALUES ('product-images', 'product-images', true)
ON CONFLICT (id) DO UPDATE SET public = true;

-- Storage Policy: Allow Public Read Access
CREATE POLICY "Public Read Access" ON storage.objects 
FOR SELECT USING (bucket_id = 'product-images');

-- Storage Policy: Allow Authenticated & Anon Uploads for Product Images
CREATE POLICY "Allow Image Uploads" ON storage.objects 
FOR INSERT WITH CHECK (bucket_id = 'product-images');

-- ====================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ====================================================================
ALTER TABLE public.items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.shops ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.loans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.loan_payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.loan_renewals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- 1. PUBLIC MARKETPLACE READ POLICIES (Only Published Items & Images)
CREATE POLICY "Marketplace Public Read Items" ON public.items
FOR SELECT USING (is_published_to_marketplace = TRUE AND status IN ('Active Loan', 'Listed', 'Purchased'));

CREATE POLICY "Marketplace Public Read Images" ON public.product_images
FOR SELECT USING (TRUE);

CREATE POLICY "Marketplace Public Read Shops" ON public.shops
FOR SELECT USING (TRUE);

-- 2. FULL ACCESS FOR ANONYMOUS / SERVICE / AUTHENTICATED ADMIN API CALLS
CREATE POLICY "Admin Full Access Items" ON public.items FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access Customers" ON public.customers FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access Loans" ON public.loans FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access Payments" ON public.loan_payments FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access Renewals" ON public.loan_renewals FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access Transactions" ON public.transactions FOR ALL USING (TRUE) WITH CHECK (TRUE);
CREATE POLICY "Admin Full Access SMS Logs" ON public.sms_logs FOR ALL USING (TRUE) WITH CHECK (TRUE);

-- ====================================================================
-- READY FOR COMMERCIAL PRODUCTION
-- ====================================================================

