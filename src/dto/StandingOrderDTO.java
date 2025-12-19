package dto;

import java.time.LocalDate;

// StandingOrderDTO.java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object for Standing Order
 * Represents a scheduled recurring payment between accounts
 */
public class StandingOrderDTO {
    private int standingOrderId;
    private int fromAccountId;
    private int toAccountId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String frequency; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextExecutionDate;
    private LocalDate lastExecutionDate;
    private String description;
    private String status; // ACTIVE, PAUSED, CANCELLED, COMPLETED
    private java.sql.Timestamp createdDate;

    /**
     * Default constructor
     */
    public StandingOrderDTO() {
    }

    /**
     * Parameterized constructor for creating new standing orders
     */
    public StandingOrderDTO(int fromAccountId, int toAccountId, double amount,
                            String frequency, LocalDate startDate, LocalDate endDate,
                            LocalDate nextExecutionDate, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextExecutionDate = nextExecutionDate;
        this.description = description;
        this.status = "ACTIVE";
    }

    // Getters and Setters

    public int getStandingOrderId() {
        return standingOrderId;
    }

    public void setStandingOrderId(int standingOrderId) {
        this.standingOrderId = standingOrderId;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(int fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(int toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public LocalDate getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(LocalDate lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.sql.Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(java.sql.Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Check if standing order is active
     */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    /**
     * Check if standing order has expired
     */
    public boolean isExpired() {
        if (endDate == null) {
            return false; // No end date means it never expires
        }
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Check if standing order is due for execution
     */
    public boolean isDueForExecution() {
        if (!isActive()) {
            return false;
        }
        return nextExecutionDate != null &&
                !LocalDate.now().isBefore(nextExecutionDate);
    }

    /**
     * Get days until next execution
     */
    public long getDaysUntilNextExecution() {
        if (nextExecutionDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextExecutionDate);
    }

    /**
     * Get total number of executions (if last execution date exists)
     */
    public int getExecutionCount() {
        if (lastExecutionDate == null) {
            return 0;
        }

        // Calculate based on frequency and dates
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, lastExecutionDate);

        switch (frequency.toUpperCase()) {
            case "DAILY":
                return (int) daysBetween;
            case "WEEKLY":
                return (int) (daysBetween / 7);
            case "MONTHLY":
                return (int) java.time.temporal.ChronoUnit.MONTHS.between(startDate, lastExecutionDate);
            case "QUARTERLY":
                return (int) (java.time.temporal.ChronoUnit.MONTHS.between(startDate, lastExecutionDate) / 3);
            case "YEARLY":
                return (int) java.time.temporal.ChronoUnit.YEARS.between(startDate, lastExecutionDate);
            default:
                return 0;
        }
    }

    /**
     * Calculate remaining executions (if end date exists)
     */
    public int getRemainingExecutions() {
        if (endDate == null) {
            return -1; // Unlimited
        }

        if (nextExecutionDate == null || nextExecutionDate.isAfter(endDate)) {
            return 0;
        }

        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(nextExecutionDate, endDate);

        switch (frequency.toUpperCase()) {
            case "DAILY":
                return (int) daysRemaining;
            case "WEEKLY":
                return (int) (daysRemaining / 7);
            case "MONTHLY":
                return (int) java.time.temporal.ChronoUnit.MONTHS.between(nextExecutionDate, endDate);
            case "QUARTERLY":
                return (int) (java.time.temporal.ChronoUnit.MONTHS.between(nextExecutionDate, endDate) / 3);
            case "YEARLY":
                return (int) java.time.temporal.ChronoUnit.YEARS.between(nextExecutionDate, endDate);
            default:
                return 0;
        }
    }

    /**
     * Get frequency in readable format
     */
    public String getFrequencyDescription() {
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return "Every day";
            case "WEEKLY":
                return "Every week";
            case "MONTHLY":
                return "Every month";
            case "QUARTERLY":
                return "Every 3 months";
            case "YEARLY":
                return "Every year";
            default:
                return frequency;
        }
    }

    /**
     * Display detailed standing order information
     */
    public void displayDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║          Standing Order Details                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("Order ID:          " + standingOrderId);
        System.out.println("From Account:      " + fromAccountNumber);
        System.out.println("To Account:        " + toAccountNumber);
        System.out.printf("Amount:            $%.2f%n", amount);
        System.out.println("Frequency:         " + getFrequencyDescription());
        System.out.println("Start Date:        " + startDate.format(formatter));

        if (endDate != null) {
            System.out.println("End Date:          " + endDate.format(formatter));
            System.out.println("Remaining Exec.:   " + getRemainingExecutions());
        } else {
            System.out.println("End Date:          No end date (Ongoing)");
        }

        System.out.println("Next Execution:    " + nextExecutionDate.format(formatter));
        System.out.println("Days Until Next:   " + getDaysUntilNextExecution() + " days");

        if (lastExecutionDate != null) {
            System.out.println("Last Execution:    " + lastExecutionDate.format(formatter));
            System.out.println("Total Executions:  " + getExecutionCount());
        } else {
            System.out.println("Last Execution:    Not yet executed");
        }

        System.out.println("Description:       " + (description != null ? description : "N/A"));
        System.out.println("Status:            " + status);
        System.out.println("════════════════════════════════════════════════════");
    }

    /**
     * String representation for listing
     */
    @Override
    public String toString() {
        return String.format("Standing Order #%d: %s → %s | $%.2f | %s | Next: %s | Status: %s",
                standingOrderId,
                fromAccountNumber != null ? fromAccountNumber : "ID:" + fromAccountId,
                toAccountNumber != null ? toAccountNumber : "ID:" + toAccountId,
                amount,
                frequency,
                nextExecutionDate,
                status);
    }

    /**
     * Compact string representation
     */
    public String toCompactString() {
        return String.format("#%d: $%.2f %s (%s)",
                standingOrderId, amount, frequency, status);
    }

    /**
     * Check equality based on ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StandingOrderDTO other = (StandingOrderDTO) obj;
        return standingOrderId == other.standingOrderId;
    }

    /**
     * Hash code based on ID
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(standingOrderId);
    }
}