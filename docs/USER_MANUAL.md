# 📖 Banking System - User Manual

Welcome to the Banking System! This manual will guide you through all features and operations.

---

## 📋 Table of Contents

1. [Getting Started](#getting-started)
2. [User Registration & Login](#user-registration--login)
3. [Account Management](#account-management)
4. [Transactions](#transactions)
5. [Standing Orders](#standing-orders)
6. [Interest Calculation](#interest-calculation)
7. [Reports & History](#reports--history)
8. [Troubleshooting](#troubleshooting)

---

## 🚀 Getting Started

### Launching the Application

**Windows:**
```bash
java -cp "bin;lib\mysql-connector-j-9.5.0.jar" app.BankingApp
```

**macOS/Linux:**
```bash
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" app.BankingApp
```

### Main Menu

Upon launching, you'll see:

```
╔════════════════════════════════════════╗
║          Banking System Menu           ║
╠════════════════════════════════════════╣
║  1. Create Account                     ║
║  2. Deposit Money                      ║
║  3. Withdraw Money                     ║
║  4. Transfer Money                     ║
║  5. Check Balance                      ║
║  6. View Transaction History           ║
║  7. Apply Interest                     ║
║  8. Display All Accounts               ║
║  9. Search Accounts                    ║
║ 10. Close Account                      ║
║ 11. Exit                               ║
╚════════════════════════════════════════╝

Enter your choice:
```

---

## 👤 User Registration & Login

### First Time Users

1. **Select Registration** (if using EnhancedBankingApp)
2. **Enter Details:**
   ```
   Username: john_doe
   Password: MyBank@2024
   Email: john@email.com
   Full Name: John Doe
   ```

3. **Password Requirements:**
    - Minimum 8 characters
    - At least 1 uppercase letter
    - At least 1 lowercase letter
    - At least 1 digit
    - At least 1 special character

4. **Confirmation:**
   ```
   ✓ User registered successfully: john_doe
   ```

### Logging In

1. **Enter Credentials:**
   ```
   Username: john_doe
   Password: MyBank@2024
   ```

2. **Success:**
   ```
   ✓ Authentication successful: john_doe
   Welcome back, John Doe!
   Last login: 2024-12-20 10:15:23
   ```

3. **Security Features:**
    - After 5 failed attempts, account is locked for 30 minutes
    - All login attempts are logged

---

## 🏦 Account Management

### Creating an Account

#### Step 1: Select Option
```
Enter your choice: 1
```

#### Step 2: Enter Holder Name
```
Enter account holder name: Alice Johnson
```

#### Step 3: Choose Account Type
```
Account Types:
1. Savings Account (4% interest, $100 min balance)
2. Checking Account (1% interest, $500 overdraft)
3. Fixed Deposit (7% interest, locked term)

Select account type (1-3): 1
```

#### Step 4: Initial Deposit
```
Enter initial deposit amount: $1000
```

#### Step 5: Confirmation
```
✓ Account created successfully!
Account Number: ACC1735123456
Account Type: Savings Account
```

### Account Types Explained

#### 1. Savings Account 💰

**Features:**
- Interest Rate: 4% annual
- Minimum Balance: $100
- Best for: Regular savings

**Restrictions:**
- Must maintain $100 minimum balance
- Cannot withdraw if it drops below minimum

**Example:**
```
Initial Deposit: $1000
Monthly Interest: $3.33 (approx)
After 1 year: $1040.74 (with compound interest)
```

#### 2. Checking Account 💳

**Features:**
- Interest Rate: 1% annual
- Overdraft Limit: $500
- Best for: Daily transactions

**Restrictions:**
- Lower interest rate
- Can go negative up to $500

**Example:**
```
Initial Deposit: $500
Can withdraw up to: $1000 (with overdraft)
Monthly Interest: $0.42 (approx)
```

#### 3. Fixed Deposit Account 📈

**Features:**
- Interest Rate: 7% annual
- Fixed Term: 6, 12, 24, 36 months
- Best for: Long-term savings

**Restrictions:**
- Cannot withdraw before maturity
- Higher penalties for early withdrawal

**Example:**
```
Initial Deposit: $5000
Term: 12 months
Maturity Date: 2025-12-20
After 1 year: $5359.62 (with compound interest)
```

### Viewing Account Details

```
Enter your choice: 5
Enter account number: ACC1001

=== Account Information ===
Account Number: ACC1001
Account Holder: Alice Johnson
Account Type: Savings Account
Current Balance: $1,345.67
Interest Rate: 4.00%
Status: ACTIVE
Created: 2024-01-15 09:30:00
```

---

## 💸 Transactions

### Depositing Money

#### Step 1: Select Deposit
```
Enter your choice: 2
```

#### Step 2: Enter Details
```
Enter account number: ACC1001
Enter deposit amount: $500
```

#### Step 3: Confirmation
```
✓ Deposited $500.00 successfully. New balance: $1,845.67
```

**Transaction Recorded:**
- Date/Time: 2024-12-20 14:30:45
- Type: DEPOSIT
- Amount: $500.00
- Balance After: $1,845.67

### Withdrawing Money

#### Step 1: Select Withdrawal
```
Enter your choice: 3
```

#### Step 2: Enter Details
```
Enter account number: ACC1001
Enter withdrawal amount: $200
```

#### Step 3: System Checks
- ✅ Sufficient balance?
- ✅ Maintains minimum balance? (for Savings)
- ✅ Within overdraft limit? (for Checking)
- ✅ Past maturity date? (for Fixed Deposit)

#### Step 4: Confirmation
```
✓ Withdrew $200.00 successfully. New balance: $1,645.67
```

**Possible Errors:**
```
✗ Insufficient funds!
✗ Cannot withdraw below minimum balance!
✗ Cannot withdraw before maturity date!
```

### Transferring Money

#### Step 1: Select Transfer
```
Enter your choice: 4
```

#### Step 2: Enter Details
```
Enter source account number: ACC1001
Enter destination account number: ACC1002
Enter transfer amount: $300
```

#### Step 3: Confirmation
```
✓ Transferred $300.00 from ACC1001 to ACC1002 successfully
```

**What Happens:**
1. $300 withdrawn from ACC1001
2. $300 deposited to ACC1002
3. Two transactions recorded
4. Both accounts updated

**Transaction Records:**
```
ACC1001 (Source):
  Type: TRANSFER_OUT
  Amount: -$300.00
  Balance After: $1,345.67

ACC1002 (Destination):
  Type: TRANSFER_IN
  Amount: +$300.00
  Balance After: $800.00
```

---

## ⏰ Standing Orders

### What are Standing Orders?

Recurring automatic payments from one account to another.

**Common Uses:**
- 💰 Monthly savings transfers
- 🏠 Rent payments
- 💳 Bill payments
- 🎓 Tuition fees

### Creating a Standing Order

#### Step 1: Access Menu
```
Select: Standing Orders → Create New
```

#### Step 2: Enter Details
```
From Account: ACC1001
To Account: ACC1002
Amount: $100
Frequency: MONTHLY
Start Date: 2024-12-20
End Date: 2025-12-20 (optional)
Description: Monthly savings transfer
```

#### Step 3: Confirmation
```
✓ Standing order created successfully!
Order ID: #5
Next Execution: 2025-01-20
```

### Frequency Options

| Frequency | Executes | Example |
|-----------|----------|---------|
| **DAILY** | Every day | Daily savings |
| **WEEKLY** | Every 7 days | Weekly allowance |
| **MONTHLY** | Same day each month | Rent, bills |
| **QUARTERLY** | Every 3 months | Quarterly payments |
| **YEARLY** | Once per year | Annual fees |

### Viewing Standing Orders

```
=== Standing Orders for ACC1001 ===
Standing Order #5: ACC1001 → ACC1002 | $100.00 | MONTHLY | Next: 2025-01-20 | Status: ACTIVE
  End Date: 2025-12-20
  Description: Monthly savings transfer
```

### Managing Standing Orders

#### Cancel a Standing Order
```
Select: Cancel Standing Order
Enter Order ID: 5

Are you sure? (yes/no): yes

✓ Standing order cancelled
```

#### Automatic Execution

The system automatically:
1. Checks for due orders every hour
2. Executes transfers on schedule
3. Updates next execution date
4. Records transactions
5. Handles failures gracefully

**Example Execution:**
```
╔════════════════════════════════════════╗
║  Processing Standing Orders            ║
╚════════════════════════════════════════╝
Time: 2025-01-20 00:00:00

Processing: Standing Order #5: ACC1001 → ACC1002 | $100.00 | MONTHLY
✓ Transferred $100.00 from ACC1001 to ACC1002 successfully
✓ Standing order processed. Next execution: 2025-02-20

=== Standing Order Processing Summary ===
Successfully Processed: 1
Failed: 0
```

---

## 📊 Interest Calculation

### Automatic Interest

Interest is calculated and applied automatically based on:
- Account type
- Interest rate
- Balance
- Time period

### Interest Rates

| Account Type | Annual Rate | Monthly Rate |
|--------------|-------------|--------------|
| Savings | 4.00% | 0.33% |
| Checking | 1.00% | 0.08% |
| Fixed Deposit | 7.00% | 0.58% |

### Manual Interest Calculation

#### For Single Account
```
Enter your choice: 7
Select option: 1 (Apply to specific account)
Enter account number: ACC1001

✓ Interest of $4.48 credited to ACC1001. New balance: $1,350.15
```

#### For All Accounts
```
Enter your choice: 7
Select option: 2 (Apply to all accounts)

=== Applying Monthly Interest to All Accounts ===

Account: ACC1001
✓ Interest of $4.48 credited. New balance: $1,350.15

Account: ACC1002
✓ Interest of $0.67 credited. New balance: $800.67

Account: ACC1003
✓ Interest of $29.17 credited. New balance: $5,029.17

=== Interest Calculation Summary ===
Accounts Processed: 3
Total Interest Paid: $34.32
```

### Interest Projections

View future account value:

```
=== Interest Projection for ACC1001 ===
Current Balance: $1,345.67
Annual Interest Rate: 4.00%

--- Projected Interest ---
Daily:     $0.15
Weekly:    $1.03
Monthly:   $4.49
Quarterly: $13.46
Yearly:    $53.83

--- Future Value (Compound Interest) ---
After 1 year:  $1,399.94
After 5 years: $1,637.58
After 10 years: $1,997.24
```

### Compound vs Simple Interest

**Simple Interest:**
```
Interest = Principal × Rate × Time
Example: $1000 × 4% × 1 year = $40
```

**Compound Interest (Monthly):**
```
Amount = Principal × (1 + Rate/12)^(12 × Years)
Example: $1000 × (1 + 0.04/12)^12 = $1,040.74
```

**Difference:**
- Simple: $1,040.00 after 1 year
- Compound: $1,040.74 after 1 year
- Extra: $0.74 (interest on interest!)

---

## 📈 Reports & History

### Transaction History

#### View Account History
```
Enter your choice: 6
Enter account number: ACC1001

=== Transaction History for ACC1001 ===
[2024-12-20 14:30:45] DEPOSIT: $500.00 | Balance: $1,845.67
[2024-12-20 12:15:30] WITHDRAWAL: $200.00 | Balance: $1,345.67
[2024-12-20 10:00:00] TRANSFER_OUT: $300.00 | Balance: $1,545.67 | To ACC1002
[2024-12-15 09:00:00] INTEREST: $4.48 | Balance: $1,845.67 | Monthly interest credit
[2024-12-01 08:00:00] INITIAL_DEPOSIT: $1,000.00 | Balance: $1,000.00 | Account opening deposit
```

### All Accounts Summary

```
Enter your choice: 8

=== All Accounts ===
ACC1001 | Alice Johnson | Savings Account | Balance: $1,845.67 | Status: ACTIVE
ACC1002 | Bob Smith | Checking Account | Balance: $800.67 | Status: ACTIVE
ACC1003 | Carol White | Fixed Deposit Account | Balance: $5,029.17 | Status: ACTIVE
```

### Search Accounts

```
Enter your choice: 9
Enter account holder name: Alice

=== Search Results ===
ACC1001 | Alice Johnson | Savings Account | Balance: $1,845.67 | Status: ACTIVE
ACC1004 | Alice Williams | Checking Account | Balance: $250.00 | Status: ACTIVE
```

---

## 🔧 Troubleshooting

### Common Issues

#### Issue 1: "Account not found"

**Cause:** Incorrect account number

**Solution:**
```
✓ Double-check account number
✓ Use "Display All Accounts" to see list
✓ Search by account holder name
```

#### Issue 2: "Insufficient funds"

**Cause:** Not enough balance for transaction

**Solution:**
```
✓ Check current balance
✓ Deposit more funds
✓ Reduce transaction amount
```

#### Issue 3: "Cannot withdraw below minimum balance"

**Cause:** Savings account requires $100 minimum

**Solution:**
```
✓ Keep at least $100 in account
✓ Transfer to checking account (no minimum)
✓ Close account to withdraw all funds
```

#### Issue 4: "Cannot withdraw before maturity date"

**Cause:** Fixed deposit hasn't matured

**Solution:**
```
✓ Wait until maturity date
✓ Check maturity date in account details
✓ Contact administrator for early withdrawal
```

#### Issue 5: "Invalid password"

**Cause:** Incorrect login credentials

**Solution:**
```
✓ Check caps lock
✓ Verify username spelling
✓ Use password recovery (if available)
✓ Contact administrator after 5 failed attempts
```

---

## 💡 Tips & Best Practices

### For Maximum Security

1. **Use strong passwords**
    - Mix uppercase, lowercase, numbers, symbols
    - Don't reuse passwords
    - Change regularly

2. **Monitor your accounts**
    - Check balance regularly
    - Review transaction history
    - Report suspicious activity

3. **Keep records**
    - Save account numbers
    - Record transaction dates
    - Keep confirmation numbers

### For Best Results

1. **Choose the right account type**
    - Savings: For growing your money
    - Checking: For frequent transactions
    - Fixed Deposit: For highest returns

2. **Use standing orders**
    - Automate regular payments
    - Never miss due dates
    - Build savings automatically

3. **Take advantage of interest**
    - Keep higher balances in savings
    - Use fixed deposits for long-term
    - Compound your returns

---

## 📞 Getting Help

### In-App Help

- **Menu Option:** Select "Help" or press 'H'
- **Status Messages:** Read all confirmation messages
- **Error Messages:** Follow suggested solutions

### External Support

- **Email:** support@bankingsystem.com
- **Documentation:** See docs/ folder
- **Issues:** GitHub Issues page

---

## 📝 Quick Reference

### Keyboard Shortcuts

| Key | Action |
|-----|--------|
| 1-11 | Menu options |
| H | Help |
| Q | Quit |
| ESC | Cancel current operation |

### Account Number Format

```
Format: ACC + Timestamp
Example: ACC1735123456
Length: 13 characters
```

### Transaction Codes

| Code | Meaning |
|------|---------|
| DEPOSIT | Money added |
| WITHDRAWAL | Money removed |
| TRANSFER_IN | Money received |
| TRANSFER_OUT | Money sent |
| INTEREST | Interest earned |
| INITIAL_DEPOSIT | Account opening |

---

**Happy Banking! 🏦💰**

*For technical details, see API_REFERENCE.md*
*For security information, see SECURITY_GUIDE.md*