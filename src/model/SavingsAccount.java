package model;

// SavingsAccount.java
public class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 100.0;

    public SavingsAccount(String accountNumber, String accountHolder, double initialDeposit) {
        super(accountNumber, accountHolder, initialDeposit);
        this.interestRate = 0.04; // 4% annual interest
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return (balance - amount) >= MIN_BALANCE;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * interestRate / 12; // Monthly interest
        balance += interest;
        addTransaction("Interest Credit", interest);
        System.out.printf("Interest of $%.2f credited. New balance: $%.2f%n", interest, balance);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public static double getMinimumBalance() {
        return MIN_BALANCE;
    }
}