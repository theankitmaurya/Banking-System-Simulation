# 🚀 Banking System - Complete Setup Guide

This guide will walk you through setting up the Banking System application from scratch.

---

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Installing Java](#installing-java)
3. [Installing MySQL](#installing-mysql)
4. [Setting Up the Database](#setting-up-the-database)
5. [Configuring the Application](#configuring-the-application)
6. [Compiling the Project](#compiling-the-project)
7. [Running the Application](#running-the-application)
8. [Troubleshooting](#troubleshooting)

---

## 💻 System Requirements

### Minimum Requirements

- **Operating System:** Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM:** 4 GB minimum (8 GB recommended)
- **Disk Space:** 500 MB for application + 2 GB for database
- **Internet Connection:** Required for initial downloads

### Software Requirements

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Running the application |
| MySQL Server | 8.0+ | Database management |
| MySQL Connector/J | 9.5.0 | JDBC driver |

---

## ☕ Installing Java

### Windows

#### Method 1: Oracle JDK

1. **Download JDK**
    - Visit: https://www.oracle.com/java/technologies/downloads/
    - Select: **Windows x64 Installer**
    - Download: `jdk-17_windows-x64_bin.exe`

2. **Install JDK**
   ```
   - Run the installer
   - Accept license agreement
   - Choose installation directory (default: C:\Program Files\Java\jdk-17)
   - Complete installation
   ```

3. **Set Environment Variables**
   ```
   - Right-click "This PC" → Properties
   - Advanced system settings → Environment Variables
   - Under System Variables, click "New"
     - Variable name: JAVA_HOME
     - Variable value: C:\Program Files\Java\jdk-17
   - Find "Path" variable → Edit → New
     - Add: %JAVA_HOME%\bin
   ```

4. **Verify Installation**
   ```bash
   java -version
   javac -version
   ```

#### Method 2: OpenJDK (Alternative)

1. Download from: https://adoptium.net/
2. Follow similar installation steps

### macOS

#### Using Homebrew (Recommended)

```bash
# Install Homebrew if not installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java
brew install openjdk@17

# Link Java
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Verify
java -version
```

### Linux (Ubuntu/Debian)

```bash
# Update package index
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk

# Verify installation
java -version
javac -version

# Set JAVA_HOME (add to ~/.bashrc)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

---

## 🗄️ Installing MySQL

### Windows

1. **Download MySQL Installer**
    - Visit: https://dev.mysql.com/downloads/installer/
    - Download: **mysql-installer-community-8.x.x.msi**

2. **Run Installer**
   ```
   Setup Type: Choose "Developer Default"
   Check Requirements: Install any missing requirements
   Installation: Click "Execute" to install
   ```

3. **Configure MySQL Server**
   ```
   Type and Networking:
   - Config Type: Development Computer
   - Port: 3306 (default)
   - Open Windows Firewall port: Yes
   
   Authentication Method:
   - Use Strong Password Encryption
   
   Accounts and Roles:
   - Set root password (REMEMBER THIS!)
   - Example: root123 (use strong password in production)
   
   Windows Service:
   - Configure as Windows Service: Yes
   - Service Name: MySQL80
   - Start at System Startup: Yes
   ```

4. **Complete Installation**
   ```
   Apply Configuration → Execute
   ```

5. **Verify Installation**
   ```bash
   # Open Command Prompt
   mysql --version
   
   # Login to MySQL
   mysql -u root -p
   # Enter your password
   
   # Exit
   exit
   ```

### macOS

#### Using Homebrew

```bash
# Install MySQL
brew install mysql

# Start MySQL service
brew services start mysql

# Secure installation
mysql_secure_installation
# Follow prompts:
# - Set root password
# - Remove anonymous users: Y
# - Disallow root login remotely: Y
# - Remove test database: Y
# - Reload privilege tables: Y

# Verify
mysql --version
mysql -u root -p
```

### Linux (Ubuntu/Debian)

```bash
# Update package index
sudo apt update

# Install MySQL Server
sudo apt install mysql-server

# Start MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure installation
sudo mysql_secure_installation
# Follow same prompts as macOS

# Verify
mysql --version
sudo mysql -u root -p
```

---

## 🔧 Setting Up the Database

### Step 1: Access MySQL

```bash
# Windows/macOS/Linux
mysql -u root -p
# Enter your root password
```

### Step 2: Create Database

```sql
-- Create the banking system database
CREATE DATABASE IF NOT EXISTS banking_system;

-- Verify creation
SHOW DATABASES;

-- You should see 'banking_system' in the list

-- Exit MySQL
exit;
```

### Step 3: Import Schema

Navigate to your project directory:

```bash
# Windows
cd C:\path\to\banking-system
mysql -u root -p banking_system < resources\enhanced_database_schema.sql

# macOS/Linux
cd /path/to/banking-system
mysql -u root -p banking_system < resources/enhanced_database_schema.sql
```

**Enter your MySQL password when prompted.**

### Step 4: Verify Tables

```bash
mysql -u root -p banking_system
```

```sql
-- List all tables
SHOW TABLES;

-- You should see:
-- accounts
-- savings_accounts
-- checking_accounts
-- fixed_deposit_accounts
-- transactions
-- users
-- customer_accounts
-- standing_orders
-- audit_log
-- password_reset_tokens
-- interest_history

-- Check sample data
SELECT * FROM accounts;
SELECT * FROM users;

-- Exit
exit;
```

---

## ⚙️ Configuring the Application

### Step 1: Create Configuration File

Navigate to `resources/` directory and create/edit `db.properties`:

```properties
# Database Connection Settings
db.url=jdbc:mysql://localhost:3306/banking_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver

# Connection Pool Settings (optional)
db.pool.minSize=5
db.pool.maxSize=20
db.pool.timeout=30000
```

**⚠️ IMPORTANT:** Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password!

### Step 2: Download JDBC Driver

1. **Download MySQL Connector/J**
    - Visit: https://dev.mysql.com/downloads/connector/j/
    - Select: **Platform Independent**
    - Download: `mysql-connector-j-9.5.0.zip`

2. **Extract and Copy**
   ```bash
   # Extract the ZIP file
   # Copy mysql-connector-j-9.5.0.jar to your project's lib/ folder
   
   # Windows
   copy mysql-connector-j-9.5.0.jar C:\path\to\banking-system\lib\
   
   # macOS/Linux
   cp mysql-connector-j-9.5.0.jar /path/to/banking-system/lib/
   ```

### Step 3: Verify Configuration

Your project structure should now look like:

```
banking-system/
├── src/
├── resources/
│   ├── db.properties          ← Configured
│   └── enhanced_database_schema.sql
├── lib/
│   └── mysql-connector-j-9.5.0.jar  ← Added
└── bin/ (will be created during compilation)
```

---

## 🔨 Compiling the Project

### Option 1: Command Line

#### Windows

```bash
# Navigate to project directory
cd C:\path\to\banking-system

# Create bin directory if it doesn't exist
mkdir bin

# Compile all source files
javac -cp "lib\mysql-connector-j-9.5.0.jar" -d bin src\config\*.java src\security\*.java src\dto\*.java src\model\*.java src\dao\*.java src\service\*.java src\scheduler\*.java src\app\*.java
```

#### macOS/Linux

```bash
# Navigate to project directory
cd /path/to/banking-system

# Create bin directory
mkdir -p bin

# Compile all source files
javac -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/config/*.java src/security/*.java src/dto/*.java src/model/*.java src/dao/*.java src/service/*.java src/scheduler/*.java src/app/*.java
```

### Option 2: IntelliJ IDEA

1. **Open Project**
   ```
   File → Open → Select banking-system folder
   ```

2. **Add JDBC Driver**
   ```
   File → Project Structure (Ctrl+Alt+Shift+S)
   Libraries → + → Java
   Select: lib/mysql-connector-j-9.5.0.jar
   OK
   ```

3. **Set JDK**
   ```
   File → Project Structure → Project
   SDK: 17 (or higher)
   Language level: 17
   ```

4. **Build Project**
   ```
   Build → Build Project (Ctrl+F9)
   ```

### Option 3: Eclipse

1. **Import Project**
   ```
   File → Import → Existing Projects into Workspace
   Select: banking-system folder
   ```

2. **Add JDBC Driver**
   ```
   Right-click project → Build Path → Add External Archives
   Select: lib/mysql-connector-j-9.5.0.jar
   ```

3. **Build Project**
   ```
   Project → Build Project
   ```

### Verify Compilation

Check for compiled classes:

```bash
# Windows
dir bin

# macOS/Linux
ls -R bin/
```

You should see `.class` files for all your Java classes.

---

## ▶️ Running the Application

### Method 1: Command Line

#### Basic Application

```bash
# Windows
java -cp "bin;lib\mysql-connector-j-9.5.0.jar" app.BankingApp

# macOS/Linux
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" app.BankingApp
```

#### Enhanced Application (with all features)

```bash
# Windows
java -cp "bin;lib\mysql-connector-j-9.5.0.jar" app.EnhancedBankingApp

# macOS/Linux
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" app.EnhancedBankingApp
```

### Method 2: IntelliJ IDEA

1. **Create Run Configuration**
   ```
   Run → Edit Configurations
   + → Application
   Name: Banking System
   Main class: app.BankingApp (or app.EnhancedBankingApp)
   ```

2. **Run**
   ```
   Click the green Run button
   or
   Shift + F10
   ```

### Method 3: Eclipse

1. **Right-click on BankingApp.java**
2. **Run As → Java Application**

### Expected Output

If everything is set up correctly, you should see:

```
✓ Database configuration loaded successfully
✓ Database connection established successfully
╔════════════════════════════════════════════╗
║     Banking System with JDBC Database      ║
║        OOP + Database Integration          ║
╚════════════════════════════════════════════╝

=== Database Configuration ===
URL: jdbc:mysql://localhost:3306/banking_system
User: root
Driver: com.mysql.cj.jdbc.Driver
Password: ********

╔════════════════════════════════════════╗
║          Banking System Menu           ║
╠════════════════════════════════════════╣
...
```

---

## 🔧 Troubleshooting

### Issue 1: "JDBC Driver not found"

**Error:**
```
✗ JDBC Driver not found: com.mysql.cj.jdbc.Driver
```

**Solution:**
```bash
# Verify JAR file exists
# Windows: dir lib\mysql-connector-j-9.5.0.jar
# Linux/Mac: ls -la lib/mysql-connector-j-9.5.0.jar

# Ensure correct classpath when running
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" app.BankingApp
```

### Issue 2: "Database connection failed"

**Error:**
```
✗ Database connection failed: Communications link failure
```

**Solutions:**

1. **Check MySQL is running**
   ```bash
   # Windows: Check Services
   # macOS: brew services list
   # Linux: sudo systemctl status mysql
   ```

2. **Verify credentials in db.properties**
   ```properties
   db.user=root
   db.password=YOUR_ACTUAL_PASSWORD  ← Check this!
   ```

3. **Test MySQL connection manually**
   ```bash
   mysql -u root -p
   # If this fails, MySQL isn't running or password is wrong
   ```

### Issue 3: "Access denied for user 'root'"

**Error:**
```
✗ Access denied for user 'root'@'localhost'
```

**Solution:**
```bash
# Reset MySQL root password
# Stop MySQL service first

# Windows
mysqld --init-file=C:\mysql-init.txt

# macOS/Linux
sudo mysqld --init-file=/tmp/mysql-init.txt

# Content of mysql-init.txt:
ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';
```

### Issue 4: "Cannot find symbol: class AccountDTO"

**Error:**
```
error: cannot find symbol
  symbol:   class AccountDTO
```

**Solution:**
```bash
# Ensure all files are compiled in correct order
javac -d bin src/dto/*.java
javac -cp "bin:lib/*" -d bin src/dao/*.java
javac -cp "bin:lib/*" -d bin src/**/*.java
```

### Issue 5: "Table doesn't exist"

**Error:**
```
✗ Table 'banking_system.accounts' doesn't exist
```

**Solution:**
```bash
# Re-run the schema script
mysql -u root -p banking_system < resources/enhanced_database_schema.sql
```

### Issue 6: Port 3306 already in use

**Error:**
```
Can't connect to MySQL server on 'localhost:3306'
```

**Solution:**
```bash
# Check what's using port 3306
# Windows
netstat -ano | findstr :3306

# macOS/Linux
sudo lsof -i :3306

# Kill the process or change MySQL port in my.cnf
```

---

## 📝 Post-Installation

### 1. Create Test Users

```bash
# Run application
java -cp "bin:lib/*" app.EnhancedBankingApp

# Register users:
Username: admin
Password: Admin@123
Email: admin@bank.com
Full Name: System Administrator
Role: ADMIN
```

### 2. Create Test Accounts

```
Account 1:
- Holder: Alice Johnson
- Type: Savings
- Initial Deposit: $1000

Account 2:
- Holder: Bob Smith
- Type: Checking
- Initial Deposit: $500
```

### 3. Test Transactions

```
1. Deposit $200 to Alice's account
2. Withdraw $100 from Bob's account
3. Transfer $300 from Alice to Bob
4. View transaction history
```

---

## ✅ Setup Checklist

- [ ] Java JDK 17+ installed and verified
- [ ] MySQL Server 8.0+ installed and running
- [ ] Database `banking_system` created
- [ ] Schema imported successfully
- [ ] Tables verified (11 tables total)
- [ ] `db.properties` configured with correct password
- [ ] MySQL Connector/J JAR in `lib/` folder
- [ ] All source files compiled successfully
- [ ] Application runs without errors
- [ ] Database connection established
- [ ] Test accounts created
- [ ] Test transactions completed

---

## 🎉 Success!

If you've completed all steps, your Banking System is now fully set up and ready to use!

For usage instructions, see: [USER_MANUAL.md](USER_MANUAL.md)

For API reference, see: [API_REFERENCE.md](API_REFERENCE.md)

---

## 📞 Need Help?

If you encounter issues not covered in this guide:

1. Check the [Troubleshooting](#troubleshooting) section
2. Review error messages carefully
3. Verify each step was completed correctly
4. Open an issue on GitHub with:
    - Your operating system
    - Java version (`java -version`)
    - MySQL version (`mysql --version`)
    - Complete error message
    - Steps you've already tried

---

**Happy Banking! 🏦💰**