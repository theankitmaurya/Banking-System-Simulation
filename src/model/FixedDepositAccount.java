package model;

// FixedDepositAccount.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FixedDepositAccount extends Account {
    private int termMonths;
    private LocalDateTime maturityDate;

    public FixedDepositAccount(String accountNumber, String accountHolder,
                               double initialDeposit, int termMonths) {
        super(accountNumber, accountHolder, initialDeposit);
        this.interestRate = 0.07; // 7% annual interest
        this.termMonths = termMonths;
        this.maturityDate = LocalDateTime.now().plusMonths(termMonths);
    }

    @Override
    protected boolean canWithdraw(double amount) {
        if (LocalDateTime.now().isBefore(maturityDate)) {
            System.out.println("Cannot withdraw before maturity date!");
            System.out.println("Maturity Date: " +
                    maturityDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            return false;
        }
        return balance >= amount;
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
        return "Fixed Deposit Account (" + termMonths + " months)";
    }

    public int getTermMonths() {
        return termMonths;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public boolean isMatured() {
        return LocalDateTime.now().isAfter(maturityDate) ||
                LocalDateTime.now().isEqual(maturityDate);
    }
}