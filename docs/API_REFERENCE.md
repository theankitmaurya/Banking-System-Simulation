# 📚 Banking System - API Reference

Complete API documentation for all classes, methods, and interfaces.

---

## 📋 Table of Contents

1. [Security Layer](#security-layer)
2. [Configuration Layer](#configuration-layer)
3. [DAO Layer](#dao-layer)
4. [DTO Layer](#dto-layer)
5. [Model Layer](#model-layer)
6. [Service Layer](#service-layer)
7. [Scheduler Layer](#scheduler-layer)

---

## 🔒 Security Layer

### SecurityUtil

**Package:** `security`  
**Purpose:** Password hashing and security utilities

#### Methods

##### `generateSalt()`
```java
public static String generateSalt()
```
Generates a cryptographically secure random salt.

**Returns:** Base64-encoded 16-byte salt string

**Example:**
```java
String salt = SecurityUtil.generateSalt();
// Returns: "Kj8fP2mQ7vB3nL..."
```

---

##### `hashPassword()`
```java
public static String hashPassword(String password, String salt)
```
Hashes password using SHA-256 with salt.

**Parameters:**
- `password` - Plain text password
- `salt` - Salt string

**Returns:** Base64-encoded hash

**Example:**
```java
String hash = SecurityUtil.hashPassword("MyPass@123", salt);
// Returns: "7f8a9b2c3d4e5f6a..."
```

---

##### `verifyPassword()`
```java
public static boolean verifyPassword(String password, String salt, String storedHash)
```
Verifies password against stored hash.

**Parameters:**
- `password` - Plain text password to verify
- `salt` - Original salt used
- `storedHash` - Hash to compare against

**Returns:** `true` if password matches, `false` otherwise

**Example:**
```java
boolean isValid = SecurityUtil.verifyPassword("MyPass@123", salt, storedHash);
```

---

##### `isPasswordStrong()`
```java
public static boolean isPasswordStrong(String password)
```
Validates password strength.

**Parameters:**
- `password` - Password to validate

**Returns:** `true` if meets all requirements

**Requirements:**
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 digit
- At least 1 special character

**Example:**
```java
boolean strong = SecurityUtil.isPasswordStrong("MyPass@123"); // true
boolean weak = SecurityUtil.isPasswordStrong("password"); // false
```

---

##### `getPasswordStrengthFeedback()`
```java
public static String getPasswordStrengthFeedback(String password)
```
Provides feedback on password strength.

**Parameters:**
- `password` - Password to check

**Returns:** Feedback message

**Example:**
```java
String feedback = SecurityUtil.getPasswordStrengthFeedback("pass");
// Returns: "Password must be at least 8 characters long"
```

---

## ⚙️ Configuration Layer

### DatabaseConfig

**Package:** `config`  
**Purpose:** Database configuration management

#### Methods

##### `getUrl()`
```java
public static String getUrl()
```
**Returns:** Database connection URL

---

##### `getUser()`
```java
public static String getUser()
```
**Returns:** Database username

---

##### `getPassword()`
```java
public static String getPassword()
```
**Returns:** Database password

---

##### `getDriver()`
```java
public static String getDriver()
```
**Returns:** JDBC driver class name

---

### DatabaseConnection

**Package:** `config`  
**Purpose:** Database connection management (Singleton)

#### Methods

##### `getInstance()`
```java
public static DatabaseConnection getInstance()
```
Gets singleton instance.

**Returns:** DatabaseConnection instance

**Example:**
```java
DatabaseConnection dbConn = DatabaseConnection.getInstance();
```

---

##### `getConnection()`
```java
public Connection getConnection()
```
**Returns:** Active database connection

---

##### `testConnection()`
```java
public boolean testConnection()
```
Tests database connectivity.

**Returns:** `true` if connection is valid

---

##### `closeConnection()`
```java
public void closeConnection()
```
Closes database connection.

---

##### `beginTransaction()`
```java
public void beginTransaction() throws SQLException
```
Begins database transaction.

---

##### `commit()`
```java
public void commit() throws SQLException
```
Commits current transaction.

---

##### `rollback()`
```java
public void rollback()
```
Rolls back current transaction.

---

## 🗄️ DAO Layer

### AccountDAO

**Package:** `dao`  
**Purpose:** Account database operations

#### Methods

##### `createAccount()`
```java
public boolean createAccount(Account account)
```
Creates new account in database.

**Parameters:**
- `account` - Account object to create

**Returns:** `true` if successful

**Example:**
```java
Account account = new SavingsAccount("ACC1001", "Alice", 1000.0);
boolean success = accountDAO.createAccount(account);
```

---

##### `getAccountByNumber()`
```java
public AccountDTO getAccountByNumber(String accountNumber)
```
Retrieves account by account number.

**Parameters:**
- `accountNumber` - Account number to search

**Returns:** AccountDTO or null if not found

---

##### `getAllAccounts()`
```java
public List<AccountDTO> getAllAccounts()
```
**Returns:** List of all accounts

---

##### `updateBalance()`
```java
public boolean updateBalance(String accountNumber, double newBalance)
```
Updates account balance.

**Parameters:**
- `accountNumber` - Account to update
- `newBalance` - New balance value

**Returns:** `true` if successful

---

##### `deleteAccount()`
```java
public boolean deleteAccount(String accountNumber)
```
Deletes account.

**Parameters:**
- `accountNumber` - Account to delete

**Returns:** `true` if successful

---

##### `getAccountsByHolder()`
```java
public List<AccountDTO> getAccountsByHolder(String holderName)
```
Searches accounts by holder name.

**Parameters:**
- `holderName` - Name to search (partial match)

**Returns:** List of matching accounts

---

### TransactionDAO

**Package:** `dao`  
**Purpose:** Transaction database operations

#### Methods

##### `recordTransaction()`
```java
public boolean recordTransaction(String accountNumber, String transactionType, 
                                double amount, double balanceAfter, String description)
```
Records a transaction.

**Parameters:**
- `accountNumber` - Account number
- `transactionType` - Type (DEPOSIT, WITHDRAWAL, etc.)
- `amount` - Transaction amount
- `balanceAfter` - Balance after transaction
- `description` - Optional description

**Returns:** `true` if successful

---

##### `recordTransfer()`
```java
public boolean recordTransfer(String fromAccountNumber, String toAccountNumber, 
                             double amount, double fromBalance, double toBalance)
```
Records a transfer transaction.

**Parameters:**
- `fromAccountNumber` - Source account
- `toAccountNumber` - Destination account
- `amount` - Transfer amount
- `fromBalance` - Source balance after
- `toBalance` - Destination balance after

**Returns:** `true` if successful

---

##### `getTransactionHistory()`
```java
public List<TransactionDTO> getTransactionHistory(String accountNumber)
```
Gets transaction history for account.

**Parameters:**
- `accountNumber` - Account number

**Returns:** List of transactions

---

##### `getRecentTransactions()`
```java
public List<TransactionDTO> getRecentTransactions(int limit)
```
Gets recent transactions across all accounts.

**Parameters:**
- `limit` - Maximum number of transactions

**Returns:** List of recent transactions

---

##### `getTransactionsByType()`
```java
public List<TransactionDTO> getTransactionsByType(String transactionType)
```
Filters transactions by type.

**Parameters:**
- `transactionType` - Type to filter

**Returns:** Filtered transaction list

---

##### `getTransactionsByDateRange()`
```java
public List<TransactionDTO> getTransactionsByDateRange(String accountNumber, 
                                                      Date startDate, Date endDate)
```
Gets transactions within date range.

**Parameters:**
- `accountNumber` - Account number
- `startDate` - Start date (inclusive)
- `endDate` - End date (inclusive)

**Returns:** Transactions in range

---

### UserDAO

**Package:** `dao`  
**Purpose:** User authentication and management

#### Methods

##### `registerUser()`
```java
public boolean registerUser(String username, String password, String email, 
                           String fullName, String role)
```
Registers new user with hashed password.

**Parameters:**
- `username` - Unique username
- `password` - Plain text password (will be hashed)
- `email` - Email address
- `fullName` - Full name
- `role` - CUSTOMER, ADMIN, or MANAGER

**Returns:** `true` if successful

**Example:**
```java
boolean success = userDAO.registerUser(
    "alice", 
    "MyPass@123", 
    "alice@email.com",
    "Alice Johnson",
    "CUSTOMER"
);
```

---

##### `authenticateUser()`
```java
public UserDTO authenticateUser(String username, String password)
```
Authenticates user login.

**Parameters:**
- `username` - Username
- `password` - Plain text password

**Returns:** UserDTO if successful, null otherwise

---

##### `changePassword()`
```java
public boolean changePassword(String username, String oldPassword, String newPassword)
```
Changes user password.

**Parameters:**
- `username` - Username
- `oldPassword` - Current password
- `newPassword` - New password

**Returns:** `true` if successful

---

##### `getUserByUsername()`
```java
public UserDTO getUserByUsername(String username)
```
Retrieves user details.

**Parameters:**
- `username` - Username to search

**Returns:** UserDTO or null

---

##### `lockUserAccount()`
```java
public boolean lockUserAccount(String username)
```
Locks user account.

**Parameters:**
- `username` - Username to lock

**Returns:** `true` if successful

---

### StandingOrderDAO

**Package:** `dao`  
**Purpose:** Standing order database operations

#### Methods

##### `createStandingOrder()`
```java
public boolean createStandingOrder(StandingOrderDTO standingOrder)
```
Creates new standing order.

**Parameters:**
- `standingOrder` - StandingOrderDTO object

**Returns:** `true` if successful

---

##### `getDueStandingOrders()`
```java
public List<StandingOrderDTO> getDueStandingOrders()
```
Gets all standing orders due for execution.

**Returns:** List of due orders

---

##### `getStandingOrdersByAccount()`
```java
public List<StandingOrderDTO> getStandingOrdersByAccount(String accountNumber)
```
Gets standing orders for account.

**Parameters:**
- `accountNumber` - Account number

**Returns:** List of standing orders

---

##### `updateNextExecutionDate()`
```java
public boolean updateNextExecutionDate(int standingOrderId, LocalDate nextDate)
```
Updates next execution date.

**Parameters:**
- `standingOrderId` - Order ID
- `nextDate` - New execution date

**Returns:** `true` if successful

---

##### `cancelStandingOrder()`
```java
public boolean cancelStandingOrder(int standingOrderId)
```
Cancels standing order.

**Parameters:**
- `standingOrderId` - Order ID to cancel

**Returns:** `true` if successful

---

## 📦 DTO Layer

### AccountDTO

**Package:** `dto`  
**Purpose:** Account data transfer

#### Fields

```java
private int accountId;
private String accountNumber;
private String accountHolder;
private String accountType;
private double balance;
private double interestRate;
private Timestamp createdDate;
private String status;
```

#### Methods

All standard getters and setters, plus:

##### `toString()`
```java
public String toString()
```
**Returns:** Formatted account string

---

### TransactionDTO

**Package:** `dto`  
**Purpose:** Transaction data transfer

#### Fields

```java
private int transactionId;
private int accountId;
private String accountNumber;
private String accountHolder;
private String transactionType;
private double amount;
private double balanceAfter;
private Timestamp transactionDate;
private String description;
```

---

### UserDTO

**Package:** `dto`  
**Purpose:** User data transfer (no password)

#### Fields

```java
private int userId;
private String username;
private String email;
private String fullName;
private String role;
private String status;
private Timestamp createdDate;
private Timestamp lastLogin;
```

#### Utility Methods

##### `isActive()`
```java
public boolean isActive()
```
**Returns:** `true` if user is active

---

##### `isLocked()`
```java
public boolean isLocked()
```
**Returns:** `true` if account is locked

---

##### `isAdmin()`
```java
public boolean isAdmin()
```
**Returns:** `true` if user has admin role

---

##### `hasAdminPrivileges()`
```java
public boolean hasAdminPrivileges()
```
**Returns:** `true` if admin or manager

---

### StandingOrderDTO

**Package:** `dto`  
**Purpose:** Standing order data transfer

#### Fields

```java
private int standingOrderId;
private int fromAccountId;
private int toAccountId;
private String fromAccountNumber;
private String toAccountNumber;
private double amount;
private String frequency;
private LocalDate startDate;
private LocalDate endDate;
private LocalDate nextExecutionDate;
private LocalDate lastExecutionDate;
private String description;
private String status;
```

#### Utility Methods

##### `isDueForExecution()`
```java
public boolean isDueForExecution()
```
**Returns:** `true` if order should execute now

---

##### `getRemainingExecutions()`
```java
public int getRemainingExecutions()
```
**Returns:** Number of remaining executions, or -1 if unlimited

---

## 🎯 Model Layer

### Account (Abstract)

**Package:** `model`  
**Purpose:** Base class for all account types

#### Methods

##### `deposit()`
```java
public void deposit(double amount)
```
Deposits money into account.

**Parameters:**
- `amount` - Amount to deposit (must be positive)

---

##### `withdraw()`
```java
public boolean withdraw(double amount)
```
Withdraws money from account.

**Parameters:**
- `amount` - Amount to withdraw

**Returns:** `true` if successful

---

##### `calculateInterest()` (Abstract)
```java
public abstract void calculateInterest()
```
Calculates and applies interest. Implementation varies by account type.

---

##### `getAccountType()` (Abstract)
```java
public abstract String getAccountType()
```
**Returns:** Account type string

---

##### `displayAccountInfo()`
```java
public void displayAccountInfo()
```
Displays account information to console.

---

##### `displayTransactionHistory()`
```java
public void displayTransactionHistory()
```
Displays transaction history to console.

---

### SavingsAccount

**Extends:** Account  
**Package:** `model`

#### Constants

```java
private static final double MIN_BALANCE = 100.0;
```

#### Methods

##### `getMinimumBalance()`
```java
public static double getMinimumBalance()
```
**Returns:** Minimum balance requirement ($100)

---

### CheckingAccount

**Extends:** Account  
**Package:** `model`

#### Fields

```java
private double overdraftLimit; // Default: $500
```

#### Methods

##### `getOverdraftLimit()`
```java
public double getOverdraftLimit()
```
**Returns:** Current overdraft limit

---

##### `setOverdraftLimit()`
```java
public void setOverdraftLimit(double overdraftLimit)
```
Sets overdraft limit.

---

### FixedDepositAccount

**Extends:** Account  
**Package:** `model`

#### Fields

```java
private int termMonths;
private LocalDateTime maturityDate;
```

#### Methods

##### `getTermMonths()`
```java
public int getTermMonths()
```
**Returns:** Term length in months

---

##### `getMaturityDate()`
```java
public LocalDateTime getMaturityDate()
```
**Returns:** Maturity date

---

##### `isMatured()`
```java
public boolean isMatured()
```
**Returns:** `true` if past maturity date

---

## 🏦 Service Layer

### BankService

**Package:** `service`  
**Purpose:** Business logic coordination

#### Methods

##### `createAccount()`
```java
public boolean createAccount(Account account)
```
Creates account in database and records initial transaction.

**Parameters:**
- `account` - Account object

**Returns:** `true` if successful

---

##### `deposit()`
```java
public boolean deposit(String accountNumber, double amount)
```
Deposits money into account.

**Parameters:**
- `accountNumber` - Account number
- `amount` - Amount to deposit

**Returns:** `true` if successful

---

##### `withdraw()`
```java
public boolean withdraw(String accountNumber, double amount)
```
Withdraws money from account.

**Parameters:**
- `accountNumber` - Account number
- `amount` - Amount to withdraw

**Returns:** `true` if successful

---

##### `transfer()`
```java
public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount)
```
Transfers money between accounts.

**Parameters:**
- `fromAccountNumber` - Source account
- `toAccountNumber` - Destination account
- `amount` - Amount to transfer

**Returns:** `true` if successful

---

##### `applyInterest()`
```java
public boolean applyInterest(String accountNumber)
```
Applies interest to specific account.

**Parameters:**
- `accountNumber` - Account number

**Returns:** `true` if successful

---

##### `applyInterestToAll()`
```java
public void applyInterestToAll()
```
Applies interest to all active accounts.

---

##### `getAccount()`
```java
public AccountDTO getAccount(String accountNumber)
```
Retrieves account details.

**Parameters:**
- `accountNumber` - Account number

**Returns:** AccountDTO or null

---

##### `getAllAccounts()`
```java
public List<AccountDTO> getAllAccounts()
```
**Returns:** List of all accounts

---

##### `getTransactionHistory()`
```java
public List<TransactionDTO> getTransactionHistory(String accountNumber)
```
Gets transaction history.

**Parameters:**
- `accountNumber` - Account number

**Returns:** List of transactions

---

## ⏰ Scheduler Layer

### InterestScheduler

**Package:** `scheduler`  
**Purpose:** Automated interest calculation

#### Enums

##### CalculationMode
```java
public enum CalculationMode {
    DAILY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}
```

#### Methods

##### `start()`
```java
public void start(long delayMillis, long periodMillis)
```
Starts scheduler with custom timing.

**Parameters:**
- `delayMillis` - Initial delay
- `periodMillis` - Period between executions

---

##### `startMonthlyScheduler()`
```java
public void startMonthlyScheduler()
```
Starts with default monthly settings.

---

##### `stop()`
```java
public void stop()
```
Stops scheduler.

---

##### `calculateAndApplyInterest()`
```java
public void calculateAndApplyInterest()
```
Calculates and applies interest to all eligible accounts.

---

##### `getInterestProjection()`
```java
public String getInterestProjection(String accountNumber)
```
Generates interest projection report.

**Parameters:**
- `accountNumber` - Account number

**Returns:** Formatted projection string

---

##### `calculateCompoundInterest()`
```java
public static double calculateCompoundInterest(double principal, double rate, 
                                               int timePeriodDays, int compoundingFrequency)
```
Calculates compound interest.

**Parameters:**
- `principal` - Initial amount
- `rate` - Annual interest rate
- `timePeriodDays` - Number of days
- `compoundingFrequency` - Times compounded per year

**Returns:** Final amount with interest

---

### StandingOrderScheduler

**Package:** `scheduler`  
**Purpose:** Automated payment processing

#### Methods

##### `start()`
```java
public void start()
```
Starts standing order scheduler.

---

##### `stop()`
```java
public void stop()
```
Stops scheduler.

---

##### `processStandingOrders()`
```java
public void processStandingOrders()
```
Processes all due standing orders.

---

##### `createStandingOrder()`
```java
public boolean createStandingOrder(String fromAccountNumber, String toAccountNumber,
                                  double amount, String frequency, LocalDate startDate,
                                  LocalDate endDate, String description)
```
Creates new standing order.

**Parameters:**
- `fromAccountNumber` - Source account
- `toAccountNumber` - Destination account
- `amount` - Transfer amount
- `frequency` - DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
- `startDate` - Start date
- `endDate` - End date (optional, can be null)
- `description` - Description

**Returns:** `true` if successful

---

##### `getStandingOrders()`
```java
public List<StandingOrderDTO> getStandingOrders(String accountNumber)
```
Gets standing orders for account.

**Parameters:**
- `accountNumber` - Account number

**Returns:** List of standing orders

---

##### `cancelStandingOrder()`
```java
public boolean cancelStandingOrder(int standingOrderId)
```
Cancels standing order.

**Parameters:**
- `standingOrderId` - Order ID

**Returns:** `true` if successful

---

## 📝 Usage Examples

### Complete Transaction Flow

```java
// 1. Initialize
BankService bankService = new BankService();

// 2. Create account
Account account = new SavingsAccount("ACC1001", "Alice", 1000.0);
bankService.createAccount(account);

// 3. Deposit
bankService.deposit("ACC1001", 500.0);

// 4. Withdraw
bankService.withdraw("ACC1001", 200.0);

// 5. Transfer
bankService.transfer("ACC1001", "ACC1002", 300.0);

// 6. Apply interest
bankService.applyInterest("ACC1001");

// 7. View history
List<TransactionDTO> history = bankService.getTransactionHistory("ACC1001");
for (TransactionDTO t : history) {
    System.out.println(t);
}
```

### User Authentication Flow

```java
// 1. Initialize
UserDAO userDAO = new UserDAO();

// 2. Register
userDAO.registerUser("alice", "MyPass@123", "alice@email.com", 
                     "Alice Johnson", "CUSTOMER");

// 3. Login
UserDTO user = userDAO.authenticateUser("alice", "MyPass@123");
if (user != null) {
    System.out.println("Login successful!");
}

// 4. Change password
userDAO.changePassword("alice", "MyPass@123", "NewPass@456");
```

### Standing Order Flow

```java
// 1. Initialize
StandingOrderScheduler scheduler = 
    new StandingOrderScheduler(standingOrderDAO, bankService);

// 2. Start scheduler
scheduler.start();

// 3. Create standing order
scheduler.createStandingOrder(
    "ACC1001",
    "ACC1002",
    100.0,
    "MONTHLY",
    LocalDate.now(),
    LocalDate.now().plusYears(1),
    "Monthly savings"
);

// 4. View orders
List<StandingOrderDTO> orders = scheduler.getStandingOrders("ACC1001");

// 5. Cancel order
scheduler.cancelStandingOrder(1);
```

---

## 📊 Error Codes & Exceptions

### Common Exceptions

| Exception | Cause | Solution |
|-----------|-------|----------|
| SQLException | Database error | Check connection |
| IllegalArgumentException | Invalid input | Validate input |
| NullPointerException | Null reference | Check for null |
| NumberFormatException | Invalid number | Validate numeric input |

---

**For implementation details, see source code in `src/` directory**

**Last Updated:** December 2024