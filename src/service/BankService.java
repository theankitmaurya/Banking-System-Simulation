package service;

// BankService.java
import config.DatabaseConnection;
import dao.AccountDAO;
import dao.TransactionDAO;
import dto.AccountDTO;
import dto.TransactionDTO;
import model.Account;
import model.SavingsAccount;

import java.util.List;

/**
 * Service layer for banking operations
 * Coordinates between domain objects and DAOs
 */
public class BankService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public BankService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Create a new account
     */
    public boolean createAccount(Account account) {
        // Save to database
        boolean created = accountDAO.createAccount(account);

        if (created) {
            // Record initial deposit transaction
            transactionDAO.recordTransaction(
                    account.getAccountNumber(),
                    "INITIAL_DEPOSIT",
                    account.getBalance(),
                    account.getBalance(),
                    "Account opening deposit"
            );
        }

        return created;
    }

    /**
     * Deposit money into account
     */
    public boolean deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("✗ Deposit amount must be positive!");
            return false;
        }

        // Get current account details
        AccountDTO accountDTO = accountDAO.getAccountByNumber(accountNumber);
        if (accountDTO == null) {
            System.out.println("✗ Account not found!");
            return false;
        }

        // Calculate new balance
        double newBalance = accountDTO.getBalance() + amount;

        // Update balance in database
        boolean updated = accountDAO.updateBalance(accountNumber, newBalance);

        if (updated) {
            // Record transaction
            transactionDAO.recordTransaction(
                    accountNumber,
                    "DEPOSIT",
                    amount,
                    newBalance,
                    "Cash deposit"
            );
            System.out.printf("✓ Deposited $%.2f successfully. New balance: $%.2f%n",
                    amount, newBalance);
            return true;
        }

        return false;
    }

    /**
     * Withdraw money from account
     */
    public boolean withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("✗ Withdrawal amount must be positive!");
            return false;
        }

        // Get current account details
        AccountDTO accountDTO = accountDAO.getAccountByNumber(accountNumber);
        if (accountDTO == null) {
            System.out.println("✗ Account not found!");
            return false;
        }

        // Check if withdrawal is allowed
        if (!canWithdraw(accountDTO, amount)) {
            System.out.println("✗ Insufficient funds or minimum balance requirement not met!");
            return false;
        }

        // Calculate new balance
        double newBalance = accountDTO.getBalance() - amount;

        // Update balance in database
        boolean updated = accountDAO.updateBalance(accountNumber, newBalance);

        if (updated) {
            // Record transaction
            transactionDAO.recordTransaction(
                    accountNumber,
                    "WITHDRAWAL",
                    amount,
                    newBalance,
                    "Cash withdrawal"
            );
            System.out.printf("✓ Withdrew $%.2f successfully. New balance: $%.2f%n",
                    amount, newBalance);
            return true;
        }

        return false;
    }

    /**
     * Transfer money between accounts
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("✗ Transfer amount must be positive!");
            return false;
        }

        // Get account details
        AccountDTO fromAccount = accountDAO.getAccountByNumber(fromAccountNumber);
        AccountDTO toAccount = accountDAO.getAccountByNumber(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            System.out.println("✗ One or both accounts not found!");
            return false;
        }

        // Check if withdrawal is allowed
        if (!canWithdraw(fromAccount, amount)) {
            System.out.println("✗ Insufficient funds in source account!");
            return false;
        }

        // Calculate new balances
        double fromNewBalance = fromAccount.getBalance() - amount;
        double toNewBalance = toAccount.getBalance() + amount;

        // Update both balances
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        try {
            dbConn.beginTransaction();

            accountDAO.updateBalance(fromAccountNumber, fromNewBalance);
            accountDAO.updateBalance(toAccountNumber, toNewBalance);

            // Record transfer transactions
            transactionDAO.recordTransfer(fromAccountNumber, toAccountNumber,
                    amount, fromNewBalance, toNewBalance);

            dbConn.commit();
            System.out.printf("✓ Transferred $%.2f from %s to %s successfully%n",
                    amount, fromAccountNumber, toAccountNumber);
            return true;

        } catch (Exception e) {
            dbConn.rollback();
            System.err.println("✗ Transfer failed: " + e.getMessage());
        }

        return false;
    }

    /**
     * Apply interest to an account
     */
    public boolean applyInterest(String accountNumber) {
        AccountDTO accountDTO = accountDAO.getAccountByNumber(accountNumber);
        if (accountDTO == null) {
            System.out.println("✗ Account not found!");
            return false;
        }

        // Calculate monthly interest
        double interest = accountDTO.getBalance() * accountDTO.getInterestRate() / 12;
        double newBalance = accountDTO.getBalance() + interest;

        // Update balance
        boolean updated = accountDAO.updateBalance(accountNumber, newBalance);

        if (updated) {
            // Record interest transaction
            transactionDAO.recordTransaction(
                    accountNumber,
                    "INTEREST",
                    interest,
                    newBalance,
                    "Monthly interest credit"
            );
            System.out.printf("✓ Interest of $%.2f credited to %s. New balance: $%.2f%n",
                    interest, accountNumber, newBalance);
            return true;
        }

        return false;
    }

    /**
     * Apply interest to all active accounts
     */
    public void applyInterestToAll() {
        List<AccountDTO> accounts = accountDAO.getAllAccounts();
        System.out.println("\n=== Applying Monthly Interest to All Accounts ===");

        for (AccountDTO account : accounts) {
            if ("ACTIVE".equals(account.getStatus())) {
                applyInterest(account.getAccountNumber());
            }
        }
    }

    /**
     * Get account details
     */
    public AccountDTO getAccount(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    /**
     * Get all accounts
     */
    public List<AccountDTO> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    /**
     * Get transaction history
     */
    public List<TransactionDTO> getTransactionHistory(String accountNumber) {
        return transactionDAO.getTransactionHistory(accountNumber);
    }

    /**
     * Display transaction history
     */
    public void displayTransactionHistory(String accountNumber) {
        List<TransactionDTO> transactions = getTransactionHistory(accountNumber);

        System.out.println("\n=== Transaction History for " + accountNumber + " ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (TransactionDTO transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    /**
     * Display all accounts
     */
    public void displayAllAccounts() {
        List<AccountDTO> accounts = getAllAccounts();

        System.out.println("\n=== All Accounts ===");
        if (accounts.isEmpty()) {
            System.out.println("No accounts in the system.");
        } else {
            for (AccountDTO account : accounts) {
                System.out.println(account);
            }
        }
    }

    /**
     * Check if withdrawal is allowed based on account type
     */
    private boolean canWithdraw(AccountDTO accountDTO, double amount) {
        String accountType = accountDTO.getAccountType();
        double currentBalance = accountDTO.getBalance();

        switch (accountType) {
            case "SAVINGS":
                // Savings account must maintain minimum balance
                return (currentBalance - amount) >= SavingsAccount.getMinimumBalance();

            case "CHECKING":
                // Checking account allows overdraft
                return (currentBalance - amount) >= -500.0; // Default overdraft limit

            case "FIXED_DEPOSIT":
                // Would need to check maturity date from database
                // For now, allow withdrawal if sufficient balance
                return currentBalance >= amount;

            default:
                return currentBalance >= amount;
        }
    }

    /**
     * Search accounts by holder name
     */
    public List<AccountDTO> searchAccounts(String holderName) {
        return accountDAO.getAccountsByHolder(holderName);
    }

    /**
     * Close account
     */
    public boolean closeAccount(String accountNumber) {
        return accountDAO.deleteAccount(accountNumber);
    }
}