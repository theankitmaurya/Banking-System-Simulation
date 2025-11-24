package model;

// CheckingAccount.java
public class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, String accountHolder, double initialDeposit) {
        super(accountNumber, accountHolder, initialDeposit);
        this.interestRate = 0.01; // 1% annual interest
        this.overdraftLimit = 500.0;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return (balance - amount) >= -overdraftLimit;
    }

    @Override
    public void calculateInterest() {
        if (balance > 0) {
            double interest = balance * interestRate / 12; // Monthly interest
            balance += interest;
            addTransaction("Interest Credit", interest);
            System.out.printf("Interest of $%.2f credited. New balance: $%.2f%n", interest, balance);
        }
    }

    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}