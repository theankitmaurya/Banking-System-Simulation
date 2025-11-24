package app;

// BankingApp.java
import config.DatabaseConfig;
import config.DatabaseConnection;
import dto.AccountDTO;
import model.Account;
import model.CheckingAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import service.BankService;

import java.util.List;
import java.util.Scanner;

/**
 * Main application class for Banking System
 * Integrates OOP concepts with JDBC database operations
 */
public class BankingApp {
    private BankService bankService;
    private Scanner scanner;
    private boolean running;

    public BankingApp() {
        this.bankService = new BankService();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Initialize the application
     */
    public void initialize() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     Banking System with JDBC Database      ║");
        System.out.println("║        OOP + Database Integration          ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // Test database connection
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (!dbConn.testConnection()) {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Please check your database configuration.");
            System.exit(1);
        }

        DatabaseConfig.displayConfig();
    }

    /**
     * Display main menu
     */
    private void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Banking System Menu           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Create Account                     ║");
        System.out.println("║  2. Deposit Money                      ║");
        System.out.println("║  3. Withdraw Money                     ║");
        System.out.println("║  4. Transfer Money                     ║");
        System.out.println("║  5. Check Balance                      ║");
        System.out.println("║  6. View Transaction History           ║");
        System.out.println("║  7. Apply Interest                     ║");
        System.out.println("║  8. Display All Accounts               ║");
        System.out.println("║  9. Search Accounts                    ║");
        System.out.println("║ 10. Close Account                      ║");
        System.out.println("║ 11. Exit                               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("\nEnter your choice: ");
    }

    /**
     * Handle create account
     */
    private void handleCreateAccount() {
        System.out.println("\n=== Create New Account ===");

        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();

        System.out.println("\nAccount Types:");
        System.out.println("1. Savings Account (4% interest, $100 min balance)");
        System.out.println("2. Checking Account (1% interest, $500 overdraft)");
        System.out.println("3. Fixed Deposit (7% interest, locked term)");
        System.out.print("Select account type (1-3): ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter initial deposit amount: $");
        double deposit = scanner.nextDouble();
        scanner.nextLine();

        String accountNumber = "ACC" + System.currentTimeMillis();
        Account account = null;

        switch (typeChoice) {
            case 1:
                if (deposit < 100) {
                    System.out.println("✗ Savings account requires minimum $100 deposit!");
                    return;
                }
                account = new SavingsAccount(accountNumber, name, deposit);
                break;

            case 2:
                account = new CheckingAccount(accountNumber, name, deposit);
                break;

            case 3:
                System.out.print("Enter term in months: ");
                int term = scanner.nextInt();
                scanner.nextLine();
                account = new FixedDepositAccount(accountNumber, name, deposit, term);
                break;

            default:
                System.out.println("✗ Invalid account type!");
                return;
        }

        if (bankService.createAccount(account)) {
            System.out.println("\n✓ Account created successfully!");
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Account Type: " + account.getAccountType());
        }
    }

    /**
     * Handle deposit
     */
    private void handleDeposit() {
        System.out.println("\n=== Deposit Money ===");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter deposit amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        bankService.deposit(accountNumber, amount);
    }

    /**
     * Handle withdrawal
     */
    private void handleWithdraw() {
        System.out.println("\n=== Withdraw Money ===");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter withdrawal amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        bankService.withdraw(accountNumber, amount);
    }

    /**
     * Handle transfer
     */
    private void handleTransfer() {
        System.out.println("\n=== Transfer Money ===");
        System.out.print("Enter source account number: ");
        String fromAccount = scanner.nextLine();

        System.out.print("Enter destination account number: ");
        String toAccount = scanner.nextLine();

        System.out.print("Enter transfer amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        bankService.transfer(fromAccount, toAccount, amount);
    }

    /**
     * Handle check balance
     */
    private void handleCheckBalance() {
        System.out.println("\n=== Check Balance ===");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        AccountDTO account = bankService.getAccount(accountNumber);
        if (account != null) {
            System.out.println("\n=== Account Information ===");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Holder: " + account.getAccountHolder());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.printf("Current Balance: $%.2f%n", account.getBalance());
            System.out.printf("Interest Rate: %.2f%%%n", account.getInterestRate() * 100);
            System.out.println("Status: " + account.getStatus());
            System.out.println("Created: " + account.getCreatedDate());
        } else {
            System.out.println("✗ Account not found!");
        }
    }

    /**
     * Handle transaction history
     */
    private void handleTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        bankService.displayTransactionHistory(accountNumber);
    }

    /**
     * Handle apply interest
     */
    private void handleApplyInterest() {
        System.out.println("\n=== Apply Interest ===");
        System.out.println("1. Apply to specific account");
        System.out.println("2. Apply to all accounts");
        System.out.print("Select option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine();
            bankService.applyInterest(accountNumber);
        } else if (choice == 2) {
            bankService.applyInterestToAll();
        }
    }

    /**
     * Handle display all accounts
     */
    private void handleDisplayAllAccounts() {
        bankService.displayAllAccounts();
    }

    /**
     * Handle search accounts
     */
    private void handleSearchAccounts() {
        System.out.println("\n=== Search Accounts ===");
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();

        List<AccountDTO> accounts = bankService.searchAccounts(name);

        System.out.println("\n=== Search Results ===");
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            for (AccountDTO account : accounts) {
                System.out.println(account);
            }
        }
    }

    /**
     * Handle close account
     */
    private void handleCloseAccount() {
        System.out.println("\n=== Close Account ===");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Are you sure you want to close this account? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            if (bankService.closeAccount(accountNumber)) {
                System.out.println("✓ Account closed successfully!");
            } else {
                System.out.println("✗ Failed to close account!");
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    /**
     * Main application loop
     */
    public void run() {
        initialize();

        while (running) {
            try {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        handleCreateAccount();
                        break;
                    case 2:
                        handleDeposit();
                        break;
                    case 3:
                        handleWithdraw();
                        break;
                    case 4:
                        handleTransfer();
                        break;
                    case 5:
                        handleCheckBalance();
                        break;
                    case 6:
                        handleTransactionHistory();
                        break;
                    case 7:
                        handleApplyInterest();
                        break;
                    case 8:
                        handleDisplayAllAccounts();
                        break;
                    case 9:
                        handleSearchAccounts();
                        break;
                    case 10:
                        handleCloseAccount();
                        break;
                    case 11:
                        running = false;
                        shutdown();
                        break;
                    default:
                        System.out.println("✗ Invalid choice! Please try again.");
                }

            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * Shutdown the application
     */
    private void shutdown() {
        System.out.println("\n=== Shutting Down ===");
        DatabaseConnection.getInstance().closeConnection();
        scanner.close();
        System.out.println("✓ Thank you for using Banking System!");
        System.out.println("Goodbye!");
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        BankingApp app = new BankingApp();
        app.run();
    }
}