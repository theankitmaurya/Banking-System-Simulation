// InterestScheduler.java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

/**
 * Automated Interest Calculation Scheduler
 * Calculates and applies interest to savings accounts based on time periods
 */
public class InterestScheduler {
    private Timer timer;
    private BankService bankService;
    private boolean isRunning;

    // Interest calculation modes
    public enum CalculationMode {
        DAILY,
        MONTHLY,
        QUARTERLY,
        YEARLY
    }

    private CalculationMode calculationMode;

    public InterestScheduler(BankService bankService) {
        this.bankService = bankService;
        this.timer = new Timer("InterestSchedulerThread", true);
        this.isRunning = false;
        this.calculationMode = CalculationMode.MONTHLY; // Default to monthly
    }

    /**
     * Start the interest calculation scheduler
     * @param delayMillis Initial delay before first execution
     * @param periodMillis Period between executions
     */
    public void start(long delayMillis, long periodMillis) {
        if (isRunning) {
            System.out.println("⚠ Interest scheduler is already running");
            return;
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    calculateAndApplyInterest();
                } catch (Exception e) {
                    System.err.println("✗ Error in interest calculation: " + e.getMessage());
                }
            }
        }, delayMillis, periodMillis);

        isRunning = true;
        System.out.println("✓ Interest scheduler started (Mode: " + calculationMode + ")");
    }

    /**
     * Start with default settings (monthly calculation)
     */
    public void startMonthlyScheduler() {
        // Run every 24 hours for simulation (in production, this would be monthly)
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        start(0, oneDayInMillis);
    }

    /**
     * Stop the interest calculation scheduler
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
            System.out.println("✓ Interest scheduler stopped");
        }
    }

    /**
     * Set calculation mode
     */
    public void setCalculationMode(CalculationMode mode) {
        this.calculationMode = mode;
    }

    /**
     * Calculate and apply interest to all eligible accounts
     */
    public void calculateAndApplyInterest() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Automated Interest Calculation        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Time: " + LocalDate.now());
        System.out.println("Mode: " + calculationMode);

        List<AccountDTO> accounts = bankService.getAllAccounts();
        int accountsProcessed = 0;
        double totalInterestPaid = 0.0;

        for (AccountDTO account : accounts) {
            if (!"ACTIVE".equals(account.getStatus())) {
                continue; // Skip inactive accounts
            }

            double interest = calculateInterest(account);

            if (interest > 0) {
                boolean applied = bankService.applyInterest(account.getAccountNumber());
                if (applied) {
                    accountsProcessed++;
                    totalInterestPaid += interest;
                }
            }
        }

        System.out.println("\n=== Interest Calculation Summary ===");
        System.out.println("Accounts Processed: " + accountsProcessed);
        System.out.printf("Total Interest Paid: $%.2f%n", totalInterestPaid);
        System.out.println("═══════════════════════════════════════\n");
    }

    /**
     * Calculate interest based on account type and calculation mode
     */
    private double calculateInterest(AccountDTO account) {
        double balance = account.getBalance();
        double annualRate = account.getInterestRate();

        // Don't calculate interest on zero or negative balances
        if (balance <= 0) {
            return 0.0;
        }

        double interest = 0.0;

        switch (calculationMode) {
            case DAILY:
                interest = balance * annualRate / 365;
                break;
            case MONTHLY:
                interest = balance * annualRate / 12;
                break;
            case QUARTERLY:
                interest = balance * annualRate / 4;
                break;
            case YEARLY:
                interest = balance * annualRate;
                break;
        }

        return interest;
    }

    /**
     * Calculate compound interest over a period
     * @param principal Initial amount
     * @param rate Annual interest rate
     * @param timePeriodDays Number of days
     * @param compoundingFrequency Times compounded per year
     * @return Final amount with compound interest
     */
    public static double calculateCompoundInterest(double principal, double rate,
                                                   int timePeriodDays, int compoundingFrequency) {
        double years = timePeriodDays / 365.0;
        return principal * Math.pow(1 + (rate / compoundingFrequency), compoundingFrequency * years);
    }

    /**
     * Calculate simple interest
     * @param principal Initial amount
     * @param rate Annual interest rate
     * @param timePeriodDays Number of days
     * @return Interest amount
     */
    public static double calculateSimpleInterest(double principal, double rate, int timePeriodDays) {
        double years = timePeriodDays / 365.0;
        return principal * rate * years;
    }

    /**
     * Calculate days between account creation and now
     */
    public static long getDaysSinceCreation(LocalDate creationDate) {
        return ChronoUnit.DAYS.between(creationDate, LocalDate.now());
    }

    /**
     * Get interest projection for next period
     */
    public String getInterestProjection(String accountNumber) {
        AccountDTO account = bankService.getAccount(accountNumber);
        if (account == null) {
            return "Account not found";
        }

        double balance = account.getBalance();
        double rate = account.getInterestRate();

        StringBuilder projection = new StringBuilder();
        projection.append("\n=== Interest Projection for ").append(accountNumber).append(" ===\n");
        projection.append(String.format("Current Balance: $%.2f%n", balance));
        projection.append(String.format("Annual Interest Rate: %.2f%%%n", rate * 100));
        projection.append("\n--- Projected Interest ---\n");
        projection.append(String.format("Daily:     $%.2f%n", balance * rate / 365));
        projection.append(String.format("Weekly:    $%.2f%n", balance * rate / 52));
        projection.append(String.format("Monthly:   $%.2f%n", balance * rate / 12));
        projection.append(String.format("Quarterly: $%.2f%n", balance * rate / 4));
        projection.append(String.format("Yearly:    $%.2f%n", balance * rate));
        projection.append("\n--- Future Value (Compound Interest) ---\n");
        projection.append(String.format("After 1 year:  $%.2f%n",
                calculateCompoundInterest(balance, rate, 365, 12)));
        projection.append(String.format("After 5 years: $%.2f%n",
                calculateCompoundInterest(balance, rate, 1825, 12)));
        projection.append(String.format("After 10 years: $%.2f%n",
                calculateCompoundInterest(balance, rate, 3650, 12)));

        return projection.toString();
    }

    /**
     * Check if scheduler is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Get current calculation mode
     */
    public CalculationMode getCalculationMode() {
        return calculationMode;
    }
}