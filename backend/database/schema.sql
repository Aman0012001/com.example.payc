-- PayC Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS payc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payc_db;

-- Users Table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE,
    password VARCHAR(255) NOT NULL,
    referral_code VARCHAR(20) UNIQUE NOT NULL,
    referred_by INT DEFAULT NULL,
    manager_level ENUM('none', 'economy', 'supreme', 'silver', 'platinum', 'gold', 'diamond', 'master') DEFAULT 'none',
    is_super_manager BOOLEAN DEFAULT FALSE,
    profile_image VARCHAR(500) DEFAULT NULL,
    wallet_balance DECIMAL(10, 2) DEFAULT 0.00,
    total_earned DECIMAL(10, 2) DEFAULT 0.00,
    total_withdrawn DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('active', 'suspended', 'banned') DEFAULT 'active',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    FOREIGN KEY (referred_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_referral_code (referral_code),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Plans Table
CREATE TABLE plans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    daily_profit DECIMAL(10, 2) NOT NULL,
    duration_days INT NOT NULL,
    total_return DECIMAL(10, 2) NOT NULL,
    min_withdrawal DECIMAL(10, 2) DEFAULT 100.00,
    status ENUM('active', 'inactive') DEFAULT 'active',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB;

-- User Plans (Purchased Plans)
CREATE TABLE user_plans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    daily_profit DECIMAL(10, 2) NOT NULL,
    total_earned DECIMAL(10, 2) DEFAULT 0.00,
    days_completed INT DEFAULT 0,
    total_days INT NOT NULL,
    status ENUM('active', 'completed', 'cancelled') DEFAULT 'active',
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    last_profit_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Tasks Table
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    task_type ENUM('video', 'survey', 'app_install', 'website_visit', 'social_media', 'product_review') NOT NULL,
    reward DECIMAL(10, 2) NOT NULL,
    duration_minutes INT DEFAULT 5,
    required_action TEXT,
    external_url VARCHAR(500),
    company_name VARCHAR(100),
    company_logo VARCHAR(500),
    max_completions INT DEFAULT 1000,
    current_completions INT DEFAULT 0,
    status ENUM('active', 'inactive', 'completed') DEFAULT 'active',
    priority INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB;

-- Task History
CREATE TABLE task_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    task_id INT NOT NULL,
    reward_earned DECIMAL(10, 2) NOT NULL,
    status ENUM('started', 'in_progress', 'completed', 'failed') DEFAULT 'started',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    proof_url VARCHAR(500),
    admin_verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_task_status (task_id, status)
) ENGINE=InnoDB;

-- Transactions Table
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_type ENUM('deposit', 'withdrawal', 'task_reward', 'plan_purchase', 'referral_bonus', 'daily_profit') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    balance_before DECIMAL(10, 2) NOT NULL,
    balance_after DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'success', 'failed', 'cancelled') DEFAULT 'pending',
    description VARCHAR(255),
    reference_id VARCHAR(100),
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_type (user_id, transaction_type),
    INDEX idx_status (status),
    INDEX idx_reference (reference_id)
) ENGINE=InnoDB;

-- Payments Table (Razorpay)
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_id INT,
    razorpay_order_id VARCHAR(100) UNIQUE,
    razorpay_payment_id VARCHAR(100) UNIQUE,
    razorpay_signature VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    status ENUM('created', 'authorized', 'captured', 'refunded', 'failed') DEFAULT 'created',
    payment_method VARCHAR(50),
    error_code VARCHAR(50),
    error_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_razorpay_order (razorpay_order_id)
) ENGINE=InnoDB;

-- Withdrawals Table
CREATE TABLE withdrawals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    bank_name VARCHAR(100),
    account_number VARCHAR(50),
    ifsc_code VARCHAR(20),
    account_holder_name VARCHAR(100),
    upi_id VARCHAR(100),
    payment_method ENUM('bank_transfer', 'upi', 'paytm', 'phonepe', 'easypaisa', 'jazzcash') NOT NULL,
    status ENUM('pending', 'approved', 'processing', 'completed', 'rejected', 'cancelled') DEFAULT 'pending',
    admin_note TEXT,
    approved_by INT,
    approved_at TIMESTAMP NULL,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_status (user_id, status),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Referrals Table
CREATE TABLE referrals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    referrer_id INT NOT NULL,
    referred_id INT NOT NULL,
    commission_earned DECIMAL(10, 2) DEFAULT 0.00,
    commission_rate DECIMAL(5, 2) DEFAULT 5.00,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (referrer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (referred_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_referrer (referrer_id),
    INDEX idx_referred (referred_id),
    UNIQUE KEY unique_referral (referrer_id, referred_id)
) ENGINE=InnoDB;

-- Notifications Table
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('info', 'success', 'warning', 'error') DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;

-- Settings Table
CREATE TABLE settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT NOT NULL,
    setting_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string',
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_key (setting_key)
) ENGINE=InnoDB;

-- Admin Users Table
CREATE TABLE admin_users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('super_admin', 'admin', 'moderator') DEFAULT 'admin',
    status ENUM('active', 'inactive') DEFAULT 'active',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Manual Deposits Table
CREATE TABLE manual_deposits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    transaction_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('easypaisa', 'jazzcash') NOT NULL,
    sender_phone VARCHAR(20),
    transaction_reference VARCHAR(100),
    proof_image VARCHAR(500),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    admin_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Salary Claims Table
CREATE TABLE salary_claims (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    status ENUM('pending', 'paid', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_monthly_claim (user_id, month),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Insert Default Settings
INSERT INTO settings (setting_key, setting_value, setting_type, description) VALUES
('min_withdrawal_amount', '100', 'number', 'Minimum withdrawal amount in INR'),
('max_withdrawal_amount', '50000', 'number', 'Maximum withdrawal amount in INR'),
('referral_commission_rate', '5', 'number', 'Referral commission percentage'),
('withdrawal_processing_time', '24-48 hours', 'string', 'Withdrawal processing time'),
('app_maintenance', 'false', 'boolean', 'App maintenance mode'),
('support_email', 'support@payc.com', 'string', 'Support email address'),
('support_phone', '+91-1234567890', 'string', 'Support phone number');

-- Insert Default Admin
INSERT INTO admin_users (username, email, password, role) VALUES
('admin', 'admin@payc.com', '$2b$10$rKvVJvH.xKZQYQKQYQKQYQKQYQKQYQKQYQKQYQKQYQKQYQKQYQKQY', 'super_admin');
-- Default password: Admin@123 (CHANGE THIS IN PRODUCTION!)

-- Insert Sample Plans
INSERT INTO plans (name, description, price, daily_profit, duration_days, total_return, status, sort_order) VALUES
('Economy', 'Perfect for beginners', 15000, 333, 30, 10000, 'active', 1),
('Supreme', 'Great returns for small investment', 30000, 666, 30, 20000, 'active', 2),
('Silver', 'Balanced investment plan', 60000, 1333, 30, 40000, 'active', 3),
('Platinum', 'Premium investment option', 120000, 2666, 30, 80000, 'active', 4),
('Gold', 'High returns guaranteed', 240000, 5333, 30, 160000, 'active', 5),
('Diamond', 'Elite investment plan', 480000, 10666, 30, 320000, 'active', 6),
('Master', 'Ultimate investment package', 960000, 21333, 30, 640000, 'active', 7);

-- Insert Sample Tasks
INSERT INTO tasks (title, description, task_type, reward, duration_minutes, company_name, status, priority) VALUES
('Watch Product Video', 'Watch a 2-minute product demonstration video', 'video', 10, 2, 'TechCorp', 'active', 1),
('Complete Survey', 'Share your opinion in a quick survey', 'survey', 25, 5, 'MarketResearch Inc', 'active', 2),
('Visit Website', 'Visit our partner website and browse for 1 minute', 'website_visit', 15, 1, 'ShopNow', 'active', 3),
('Follow on Instagram', 'Follow our partner on Instagram', 'social_media', 20, 1, 'FashionBrand', 'active', 4);
