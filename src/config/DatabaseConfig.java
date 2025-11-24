package config;

// DatabaseConfig.java
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database Configuration Manager
 * Loads database connection properties from config file
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "db.properties";
    private static Properties properties = new Properties();

    // Default configuration
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        loadProperties();
    }

    /**
     * Load properties from configuration file
     */
    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            System.out.println("✓ Database configuration loaded successfully");
        } catch (IOException e) {
            System.out.println("⚠ Configuration file not found. Using default settings.");
            setDefaultProperties();
        }
    }

    /**
     * Set default properties if config file is not available
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.url", DEFAULT_URL);
        properties.setProperty("db.user", DEFAULT_USER);
        properties.setProperty("db.password", DEFAULT_PASSWORD);
        properties.setProperty("db.driver", DEFAULT_DRIVER);
    }

    /**
     * Get database URL
     */
    public static String getUrl() {
        return properties.getProperty("db.url", DEFAULT_URL);
    }

    /**
     * Get database username
     */
    public static String getUser() {
        return properties.getProperty("db.user", DEFAULT_USER);
    }

    /**
     * Get database password
     */
    public static String getPassword() {
        return properties.getProperty("db.password", DEFAULT_PASSWORD);
    }

    /**
     * Get JDBC driver class name
     */
    public static String getDriver() {
        return properties.getProperty("db.driver", DEFAULT_DRIVER);
    }

    /**
     * Display current configuration (without password)
     */
    public static void displayConfig() {
        System.out.println("\n=== Database Configuration ===");
        System.out.println("URL: " + getUrl());
        System.out.println("User: " + getUser());
        System.out.println("Driver: " + getDriver());
        System.out.println("Password: " + (getPassword().isEmpty() ? "(empty)" : "********"));
    }
}
