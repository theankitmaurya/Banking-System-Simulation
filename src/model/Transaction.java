package model;

// Transaction.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Transaction {
    private String type;
    private double amount;
    private LocalDateTime timestamp;
    private double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: $%.2f | Balance: $%.2f",
                timestamp.format(formatter), type, amount, balanceAfter);
    }
}