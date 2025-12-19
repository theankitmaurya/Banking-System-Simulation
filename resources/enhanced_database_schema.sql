-- enhanced_database_schema.sql
-- Enhanced Banking System Database Schema with Security and Time-based Features

CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

-- ============================================
-- CORE TABLES (with security enhancements)
-- ============================================

-- Table: users (with secure password storage)
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       password_salt VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role ENUM('CUSTOMER', 'ADMIN', 'MANAGER') DEFAULT 'CUSTOMER',
                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP NULL,
                       failed_login_attempts INT DEFAULT 0,
                       account_locked_until TIMESTAMP NULL,
                       status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
                       INDEX idx_username (username),
                       INDEX idx_email (email),
                       INDEX idx_status (status)
);

-- Table: accounts
CREATE TABLE accounts (
                          account_id INT PRIMARY KEY AUTO_INCREMENT,
                          account_number VARCHAR(20) UNIQUE NOT NULL,
                          account_holder VARCHAR(100) NOT NULL,
                          account_type ENUM('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT') NOT NULL,
                          balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                          interest_rate DECIMAL(5, 4) NOT NULL,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_interest_date DATE NULL,
                          status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
                          INDEX idx_account_number (account_number),
                          INDEX idx_account_holder (account_holder),
                          INDEX idx_status (status)
);

-- Table: savings_accounts
CREATE TABLE savings_accounts (
                                  account_id INT PRIMARY KEY,
                                  minimum_balance DECIMAL(15, 2) NOT NULL DEFAULT 100.00,
                                  FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: checking_accounts
CREATE TABLE checking_accounts (
                                   account_id INT PRIMARY KEY,
                                   overdraft_limit DECIMAL(15, 2) NOT NULL DEFAULT 500.00,
                                   FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: fixed_deposit_accounts
CREATE TABLE fixed_deposit_accounts (
                                        account_id INT PRIMARY KEY,
                                        term_months INT NOT NULL,
                                        maturity_date DATE NOT NULL,
                                        FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: transactions
CREATE TABLE transactions (
                              transaction_id INT PRIMARY KEY AUTO_INCREMENT,
                              account_id INT NOT NULL,
                              transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT',
                          'INTEREST', 'INITIAL_DEPOSIT', 'STANDING_ORDER') NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              balance_after DECIMAL(15, 2) NOT NULL,
                              transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              description VARCHAR(255),
                              reference_account_id INT NULL,
                              standing_order_id INT NULL,
                              INDEX idx_account_id (account_id),
                              INDEX idx_transaction_date (transaction_date),
                              INDEX idx_transaction_type (transaction_type),
                              FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
                              FOREIGN KEY (reference_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Table: customer_accounts (links users to accounts)
CREATE TABLE customer_accounts (
                                   user_id INT NOT NULL,
                                   account_id INT NOT NULL,
                                   relationship ENUM('PRIMARY', 'JOINT', 'BENEFICIARY') DEFAULT 'PRIMARY',
                                   PRIMARY KEY (user_id, account_id),
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                   FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- ============================================
-- TIME-BASED FEATURES TABLES
-- ============================================

-- Table: standing_orders (scheduled payments)
CREATE TABLE standing_orders (
                                 standing_order_id INT PRIMARY KEY AUTO_INCREMENT,
                                 from_account_id INT NOT NULL,
                                 to_account_id INT NOT NULL,
                                 amount DECIMAL(15, 2) NOT NULL,
                                 frequency ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY') NOT NULL,
                                 start_date DATE NOT NULL,
                                 end_date DATE NULL,
                                 next_execution_date DATE NOT NULL,
                                 last_execution_date DATE NULL,
                                 description VARCHAR(255),
                                 status ENUM('ACTIVE', 'PAUSED', 'CANCELLED', 'COMPLETED') DEFAULT 'ACTIVE',
                                 created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 INDEX idx_next_execution (next_execution_date),
                                 INDEX idx_status (status),
                                 FOREIGN KEY (from_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
                                 FOREIGN KEY (to_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: interest_history (track interest calculations)
CREATE TABLE interest_history (
                                  interest_id INT PRIMARY KEY AUTO_INCREMENT,
                                  account_id INT NOT NULL,
                                  interest_amount DECIMAL(15, 2) NOT NULL,
                                  balance_before DECIMAL(15, 2) NOT NULL,
                                  balance_after DECIMAL(15, 2) NOT NULL,
                                  interest_rate DECIMAL(5, 4) NOT NULL,
                                  calculation_date DATE NOT NULL,
                                  calculation_period ENUM('DAILY', 'MONTHLY', 'QUARTERLY', 'YEARLY') NOT NULL,
                                  INDEX idx_account_id (account_id),
                                  INDEX idx_calculation_date (calculation_date),
                                  FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- ============================================
-- SECURITY TABLES
-- ============================================

-- Table: audit_log (security audit trail)
CREATE TABLE audit_log (
                           audit_id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id INT NULL,
                           action_type ENUM('LOGIN', 'LOGOUT', 'CREATE_ACCOUNT', 'DELETE_ACCOUNT',
                     'TRANSFER', 'DEPOSIT', 'WITHDRAWAL', 'PASSWORD_CHANGE',
                     'FAILED_LOGIN', 'ACCOUNT_LOCKED') NOT NULL,
                           account_id INT NULL,
                           ip_address VARCHAR(45),
                           details TEXT,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_user_id (user_id),
                           INDEX idx_action_type (action_type),
                           INDEX idx_timestamp (timestamp),
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
                           FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Table: password_reset_tokens (for password recovery)
CREATE TABLE password_reset_tokens (
                                       token_id INT PRIMARY KEY AUTO_INCREMENT,
                                       user_id INT NOT NULL,
                                       token VARCHAR(255) UNIQUE NOT NULL,
                                       expiry_date TIMESTAMP NOT NULL,
                                       is_used BOOLEAN DEFAULT FALSE,
                                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- VIEWS FOR REPORTING
-- ============================================

-- View: account_summary
CREATE VIEW account_summary AS
SELECT
    a.account_number,
    a.account_holder,
    a.account_type,
    a.balance,
    a.interest_rate,
    a.created_date,
    a.status,
    COUNT(DISTINCT t.transaction_id) as total_transactions,
    COALESCE(SUM(CASE WHEN t.transaction_type = 'INTEREST' THEN t.amount ELSE 0 END), 0) as total_interest_earned
FROM accounts a
         LEFT JOIN transactions t ON a.account_id = t.account_id
GROUP BY a.account_id;

-- View: recent_transactions
CREATE VIEW recent_transactions AS
SELECT
    t.transaction_id,
    a.account_number,
    a.account_holder,
    t.transaction_type,
    t.amount,
    t.balance_after,
    t.transaction_date,
    t.description
FROM transactions t
         JOIN accounts a ON t.account_id = a.account_id
ORDER BY t.transaction_date DESC
    LIMIT 100;

-- View: active_standing_orders
CREATE VIEW active_standing_orders AS
SELECT
    so.standing_order_id,
    a1.account_number as from_account,
    a2.account_number as to_account,
    so.amount,
    so.frequency,
    so.next_execution_date,
    so.description
FROM standing_orders so
         JOIN accounts a1 ON so.from_account_id = a1.account_id
         JOIN accounts a2 ON so.to_account_id = a2.account_id
WHERE so.status = 'ACTIVE';

-- ============================================
-- STORED PROCEDURES
-- ============================================

-- Procedure: Apply interest to all savings accounts
DELIMITER //
CREATE PROCEDURE apply_monthly_interest()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE acc_id INT;
    DECLARE acc_balance DECIMAL(15, 2);
    DECLARE acc_rate DECIMAL(5, 4);
    DECLARE interest_amt DECIMAL(15, 2);

    DECLARE cur CURSOR FOR
SELECT account_id, balance, interest_rate
FROM accounts
WHERE status = 'ACTIVE' AND balance > 0;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

OPEN cur;

read_loop: LOOP
        FETCH cur INTO acc_id, acc_balance, acc_rate;
        IF done THEN
            LEAVE read_loop;
END IF;

        -- Calculate monthly interest
        SET interest_amt = acc_balance * acc_rate / 12;

        -- Update balance
UPDATE accounts
SET balance = balance + interest_amt,
    last_interest_date = CURDATE()
WHERE account_id = acc_id;

-- Record transaction
INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description)
VALUES (acc_id, 'INTEREST', interest_amt, acc_balance + interest_amt, 'Monthly interest credit');

-- Record in interest history
INSERT INTO interest_history (account_id, interest_amount, balance_before, balance_after,
                              interest_rate, calculation_date, calculation_period)
VALUES (acc_id, interest_amt, acc_balance, acc_balance + interest_amt,
        acc_rate, CURDATE(), 'MONTHLY');
END LOOP;

CLOSE cur;
END //
DELIMITER ;

-- ============================================
-- SAMPLE DATA WITH SECURITY
-- ============================================

-- Insert users with hashed passwords
-- Note: In production, use SecurityUtil.hashPassword() from Java
-- These are example hashes - replace with actual hashed passwords
INSERT INTO users (username, password_hash, password_salt, email, full_name, role) VALUES
                                                                                       ('admin', 'hash_value_here', 'salt_value_here', 'admin@bank.com', 'System Administrator', 'ADMIN'),
                                                                                       ('alice', 'hash_value_here', 'salt_value_here', 'alice@email.com', 'Alice Johnson', 'CUSTOMER'),
                                                                                       ('bob', 'hash_value_here', 'salt_value_here', 'bob@email.com', 'Bob Smith', 'CUSTOMER');

-- Insert sample accounts
INSERT INTO accounts (account_number, account_holder, account_type, balance, interest_rate, status) VALUES
                                                                                                        ('ACC1001', 'Alice Johnson', 'SAVINGS', 1000.00, 0.0400, 'ACTIVE'),
                                                                                                        ('ACC1002', 'Bob Smith', 'CHECKING', 500.00, 0.0100, 'ACTIVE'),
                                                                                                        ('ACC1003', 'Alice Johnson', 'FIXED_DEPOSIT', 5000.00, 0.0700, 'ACTIVE');

-- Insert account-specific details
INSERT INTO savings_accounts (account_id, minimum_balance) VALUES (1, 100.00);
INSERT INTO checking_accounts (account_id, overdraft_limit) VALUES (2, 500.00);
INSERT INTO fixed_deposit_accounts (account_id, term_months, maturity_date)
VALUES (3, 12, DATE_ADD(CURDATE(), INTERVAL 12 MONTH));

-- Link users to accounts
INSERT INTO customer_accounts (user_id, account_id, relationship) VALUES
                                                                      (2, 1, 'PRIMARY'),
                                                                      (3, 2, 'PRIMARY'),
                                                                      (2, 3, 'PRIMARY');

-- Insert initial transactions
INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description) VALUES
                                                                                                (1, 'INITIAL_DEPOSIT', 1000.00, 1000.00, 'Account opening deposit'),
                                                                                                (2, 'INITIAL_DEPOSIT', 500.00, 500.00, 'Account opening deposit'),
                                                                                                (3, 'INITIAL_DEPOSIT', 5000.00, 5000.00, 'Fixed deposit opening');

-- Sample standing order
INSERT INTO standing_orders (from_account_id, to_account_id, amount, frequency,
                             start_date, next_execution_date, description, status)
VALUES (1, 2, 100.00, 'MONTHLY', CURDATE(), CURDATE(), 'Monthly transfer to Bob', 'ACTIVE');

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_accounts_created_date ON accounts(created_date);
CREATE INDEX idx_transactions_amount ON transactions(amount);
CREATE INDEX idx_standing_orders_from_account ON standing_orders(from_account_id);
CREATE INDEX idx_standing_orders_to_account ON standing_orders(to_account_id);
CREATE INDEX idx_audit_log_user_timestamp ON audit_log(user_id, timestamp);

-- ============================================
-- TRIGGERS FOR SECURITY
-- ============================================

-- Trigger: Log account creation
DELIMITER //
CREATE TRIGGER after_account_insert
    AFTER INSERT ON accounts
    FOR EACH ROW
BEGIN
    INSERT INTO audit_log (action_type, account_id, details)
    VALUES ('CREATE_ACCOUNT', NEW.account_id,
            CONCAT('Account ', NEW.account_number, ' created for ', NEW.account_holder));
END //
DELIMITER ;

-- Trigger: Log account deletion
DELIMITER //
CREATE TRIGGER before_account_delete
    BEFORE DELETE ON accounts
    FOR EACH ROW
BEGIN
    INSERT INTO audit_log (action_type, account_id, details)
    VALUES ('DELETE_ACCOUNT', OLD.account_id,
            CONCAT('Account ', OLD.account_number, ' deleted'));
END //
DELIMITER ;

SHOW TABLES;