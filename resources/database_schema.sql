-- database_schema.sql
-- Banking System Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

-- Table: accounts
CREATE TABLE accounts (
                          account_id INT PRIMARY KEY AUTO_INCREMENT,
                          account_number VARCHAR(20) UNIQUE NOT NULL,
                          account_holder VARCHAR(100) NOT NULL,
                          account_type ENUM('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT') NOT NULL,
                          balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                          interest_rate DECIMAL(5, 4) NOT NULL,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
                          INDEX idx_account_number (account_number),
                          INDEX idx_account_holder (account_holder)
);

-- Table: savings_accounts (extends accounts)
CREATE TABLE savings_accounts (
                                  account_id INT PRIMARY KEY,
                                  minimum_balance DECIMAL(15, 2) NOT NULL DEFAULT 100.00,
                                  FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: checking_accounts (extends accounts)
CREATE TABLE checking_accounts (
                                   account_id INT PRIMARY KEY,
                                   overdraft_limit DECIMAL(15, 2) NOT NULL DEFAULT 500.00,
                                   FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Table: fixed_deposit_accounts (extends accounts)
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
                              transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'INTEREST', 'INITIAL_DEPOSIT') NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              balance_after DECIMAL(15, 2) NOT NULL,
                              transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              description VARCHAR(255),
                              reference_account_id INT NULL,
                              INDEX idx_account_id (account_id),
                              INDEX idx_transaction_date (transaction_date),
                              FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
                              FOREIGN KEY (reference_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Table: users (for authentication)
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       role ENUM('CUSTOMER', 'ADMIN', 'MANAGER') DEFAULT 'CUSTOMER',
                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP NULL,
                       status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE'
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

-- Sample Data for Testing
INSERT INTO users (username, password_hash, email, full_name, role) VALUES
                                                                        ('admin', 'admin123', 'admin@bank.com', 'System Administrator', 'ADMIN'),
                                                                        ('alice', 'alice123', 'alice@email.com', 'Alice Johnson', 'CUSTOMER'),
                                                                        ('bob', 'bob123', 'bob@email.com', 'Bob Smith', 'CUSTOMER');

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

-- Views for reporting
CREATE VIEW account_summary AS
SELECT
    a.account_number,
    a.account_holder,
    a.account_type,
    a.balance,
    a.interest_rate,
    a.created_date,
    a.status,
    COUNT(t.transaction_id) as total_transactions
FROM accounts a
         LEFT JOIN transactions t ON a.account_id = t.account_id
GROUP BY a.account_id;

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
    LIMIT 50;