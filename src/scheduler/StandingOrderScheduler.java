package scheduler;

// StandingOrderScheduler.java
import dao.StandingOrderDAO;
import dto.AccountDTO;
import dto.StandingOrderDTO;
import service.BankService;

import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// StandingOrderScheduler.java
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// Import the standalone DTO classes
// Make sure these files are in the correct package/directory

/**
 * Automated Standing Order Processor
 * Executes scheduled payments automatically
 */
public class StandingOrderScheduler {
    private Timer timer;
    private StandingOrderDAO standingOrderDAO;
    private BankService bankService;
    private boolean isRunning;

    public StandingOrderScheduler(StandingOrderDAO standingOrderDAO, BankService bankService) {
        this.standingOrderDAO = standingOrderDAO;
        this.bankService = bankService;
        this.timer = new Timer("StandingOrderSchedulerThread", true);
        this.isRunning = false;
    }

    /**
     * Start the standing order scheduler
     * Checks every hour for due payments
     */
    public void start() {
        if (isRunning) {
            System.out.println("⚠ Standing order scheduler is already running");
            return;
        }

        // Check every hour (in production)
        // For testing, check every minute: 60 * 1000
        long checkIntervalMillis = 60 * 60 * 1000; // 1 hour

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    processStandingOrders();
                } catch (Exception e) {
                    System.err.println("✗ Error processing standing orders: " + e.getMessage());
                }
            }
        }, 0, checkIntervalMillis);

        isRunning = true;
        System.out.println("✓ Standing order scheduler started");
    }

    /**
     * Stop the scheduler
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
            System.out.println("✓ Standing order scheduler stopped");
        }
    }

    /**
     * Process all due standing orders
     */
    public void processStandingOrders() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Processing Standing Orders            ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Time: " + LocalDate.now());

        // Get all due standing orders
        List<StandingOrderDTO> dueOrders = standingOrderDAO.getDueStandingOrders();

        if (dueOrders.isEmpty()) {
            System.out.println("No standing orders due for processing.");
            return;
        }

        System.out.println("Found " + dueOrders.size() + " standing order(s) to process\n");

        int successCount = 0;
        int failCount = 0;

        for (StandingOrderDTO order : dueOrders) {
            if (processStandingOrder(order)) {
                successCount++;
            } else {
                failCount++;
            }
        }

        // Complete expired orders
        int completedCount = standingOrderDAO.completeExpiredStandingOrders();

        System.out.println("\n=== Standing Order Processing Summary ===");
        System.out.println("Successfully Processed: " + successCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Completed (Expired): " + completedCount);
        System.out.println("═══════════════════════════════════════\n");
    }

    /**
     * Process a single standing order
     */
    private boolean processStandingOrder(StandingOrderDTO order) {
        System.out.println("\nProcessing: " + order);

        try {
            // Execute the transfer
            boolean success = bankService.transfer(
                    order.getFromAccountNumber(),
                    order.getToAccountNumber(),
                    order.getAmount()
            );

            if (success) {
                // Calculate next execution date
                LocalDate nextDate = calculateNextExecutionDate(
                        order.getNextExecutionDate(),
                        order.getFrequency()
                );

                // Check if next date exceeds end date
                if (order.getEndDate() != null && nextDate.isAfter(order.getEndDate())) {
                    // Complete the standing order
                    standingOrderDAO.cancelStandingOrder(order.getStandingOrderId());
                    System.out.println("✓ Standing order completed (end date reached)");
                } else {
                    // Update next execution date
                    standingOrderDAO.updateNextExecutionDate(
                            order.getStandingOrderId(),
                            nextDate
                    );
                    System.out.println("✓ Standing order processed. Next execution: " + nextDate);
                }

                return true;
            } else {
                System.err.println("✗ Failed to execute standing order payment");
                return false;
            }

        } catch (Exception e) {
            System.err.println("✗ Error processing standing order: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculate next execution date based on frequency
     */
    private LocalDate calculateNextExecutionDate(LocalDate currentDate, String frequency) {
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return currentDate.plusDays(1);
            case "WEEKLY":
                return currentDate.plusWeeks(1);
            case "MONTHLY":
                return currentDate.plusMonths(1);
            case "QUARTERLY":
                return currentDate.plusMonths(3);
            case "YEARLY":
                return currentDate.plusYears(1);
            default:
                return currentDate.plusMonths(1); // Default to monthly
        }
    }

    /**
     * Create a new standing order
     */
    public boolean createStandingOrder(String fromAccountNumber, String toAccountNumber,
                                       double amount, String frequency, LocalDate startDate,
                                       LocalDate endDate, String description) {
        // Validate accounts exist
        AccountDTO fromAccount = bankService.getAccount(fromAccountNumber);
        AccountDTO toAccount = bankService.getAccount(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            System.out.println("✗ One or both accounts not found");
            return false;
        }

        // Validate frequency
        if (!isValidFrequency(frequency)) {
            System.out.println("✗ Invalid frequency. Use: DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY");
            return false;
        }

        // Get account IDs
        int fromAccountId = standingOrderDAO.getAccountId(fromAccountNumber);
        int toAccountId = standingOrderDAO.getAccountId(toAccountNumber);

        // Create standing order DTO
        StandingOrderDTO order = new StandingOrderDTO();
        order.setFromAccountId(fromAccountId);
        order.setToAccountId(toAccountId);
        order.setAmount(amount);
        order.setFrequency(frequency.toUpperCase());
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setNextExecutionDate(startDate);
        order.setDescription(description);

        // Save to database
        return standingOrderDAO.createStandingOrder(order);
    }

    /**
     * Validate frequency value
     */
    private boolean isValidFrequency(String frequency) {
        String[] validFrequencies = {"DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"};
        for (String valid : validFrequencies) {
            if (valid.equalsIgnoreCase(frequency)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get standing orders for an account
     */
    public List<StandingOrderDTO> getStandingOrders(String accountNumber) {
        return standingOrderDAO.getStandingOrdersByAccount(accountNumber);
    }

    /**
     * Cancel a standing order
     */
    public boolean cancelStandingOrder(int standingOrderId) {
        return standingOrderDAO.cancelStandingOrder(standingOrderId);
    }

    /**
     * Display standing orders for an account
     */
    public void displayStandingOrders(String accountNumber) {
        List<StandingOrderDTO> orders = getStandingOrders(accountNumber);

        System.out.println("\n=== Standing Orders for " + accountNumber + " ===");
        if (orders.isEmpty()) {
            System.out.println("No standing orders found.");
        } else {
            for (StandingOrderDTO order : orders) {
                System.out.println(order);
                if (order.getEndDate() != null) {
                    System.out.println("  End Date: " + order.getEndDate());
                }
                if (order.getLastExecutionDate() != null) {
                    System.out.println("  Last Executed: " + order.getLastExecutionDate());
                }
                System.out.println("  Description: " + order.getDescription());
                System.out.println();
            }
        }
    }

    /**
     * Check if scheduler is running
     */
    public boolean isRunning() {
        return isRunning;
    }
}