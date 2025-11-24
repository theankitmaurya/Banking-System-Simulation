package config;

// DatabaseConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager using Singleton Pattern
 * Manages JDBC connections to the database
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
        try {
            // Load JDBC driver
            Class.forName(DatabaseConfig.getDriver());

            // Establish connection
            this.connection = DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
            );

            System.out.println("✓ Database connection established successfully");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ JDBC Driver not found: " + e.getMessage());
            System.err.println("Please add MySQL Connector/J to your classpath");
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            System.err.println("Please check your database configuration");
        }
    }

    /**
     * Get singleton instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get the active connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                // Reconnect
                connection = DriverManager.getConnection(
                        DatabaseConfig.getUrl(),
                        DatabaseConfig.getUser(),
                        DatabaseConfig.getPassword()
                );
                System.out.println("✓ Database reconnected successfully");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking connection: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Begin transaction
     */
    public void beginTransaction() throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    /**
     * Commit transaction
     */
    public void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    /**
     * Rollback transaction
     */
    public void rollback() {
        try {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error during rollback: " + e.getMessage());
        }
    }
}