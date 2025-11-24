package model;

// Account.java
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected List<Transaction> transactionHistory;
    protected double interestRate;

    public Account(String accountNumber, String accountHolder, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Initial Deposit", initialDeposit);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive!");
            return;
        }
        balance += amount;
        addTransaction("Deposit", amount);
        System.out.printf("Deposited $%.2f successfully. New balance: $%.2f%n", amount, balance);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive!");
            return false;
        }
        if (canWithdraw(amount)) {
            balance -= amount;
            addTransaction("Withdrawal", amount);
            System.out.printf("Withdrew $%.2f successfully. New balance: $%.2f%n", amount, balance);
            return true;
        }
        System.out.println("Insufficient funds!");
        return false;
    }

    protected abstract boolean canWithdraw(double amount);

    public abstract void calculateInterest();

    public abstract String getAccountType();

    protected void addTransaction(String type, double amount) {
        transactionHistory.add(new Transaction(type, amount, balance));
    }

    public void displayTransactionHistory() {
        System.out.println("\n=== Transaction History for " + accountNumber + " ===");
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : transactionHistory) {
                System.out.println(t);
            }
        }
    }

    public void displayAccountInfo() {
        System.out.println("\n=== Account Information ===");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Account Type: " + getAccountType());
        System.out.printf("Current Balance: $%.2f%n", balance);
        System.out.printf("Interest Rate: %.2f%%%n", interestRate * 100);
    }
}