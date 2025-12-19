package dao;

import config.DatabaseConnection;
import dto.StandingOrderDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Standing Orders
 */
// StandingOrderDAO.java
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Import the standalone DTO class
// Make sure StandingOrderDTO.java is in the same package or imported correctly

/**
 * Data Access Object for Standing Orders (Scheduled Payments)
 * Implements recurring payment functionality
 */
public class StandingOrderDAO {
    private Connection connection;

    public StandingOrderDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Create a new standing order
     */
    public boolean createStandingOrder(StandingOrderDTO standingOrder) {
        String sql = "INSERT INTO standing_orders (from_account_id, to_account_id, amount, " +
                "frequency, start_date, end_date, next_execution_date, description, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, standingOrder.getFromAccountId());
            pstmt.setInt(2, standingOrder.getToAccountId());
            pstmt.setDouble(3, standingOrder.getAmount());
            pstmt.setString(4, standingOrder.getFrequency());
            pstmt.setDate(5, Date.valueOf(standingOrder.getStartDate()));

            if (standingOrder.getEndDate() != null) {
                pstmt.setDate(6, Date.valueOf(standingOrder.getEndDate()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }

            pstmt.setDate(7, Date.valueOf(standingOrder.getNextExecutionDate()));
            pstmt.setString(8, standingOrder.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        standingOrder.setStandingOrderId(generatedKeys.getInt(1));
                        System.out.println("✓ Standing order created successfully");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating standing order: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all active standing orders due for execution
     */
    public List<StandingOrderDTO> getDueStandingOrders() {
        List<StandingOrderDTO> orders = new ArrayList<>();

        String sql = "SELECT so.*, " +
                "a1.account_number as from_account_number, " +
                "a2.account_number as to_account_number " +
                "FROM standing_orders so " +
                "JOIN accounts a1 ON so.from_account_id = a1.account_id " +
                "JOIN accounts a2 ON so.to_account_id = a2.account_id " +
                "WHERE so.status = 'ACTIVE' " +
                "AND so.next_execution_date <= CURDATE()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToStandingOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving due standing orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Get standing orders for a specific account
     */
    public List<StandingOrderDTO> getStandingOrdersByAccount(String accountNumber) {
        List<StandingOrderDTO> orders = new ArrayList<>();

        String sql = "SELECT so.*, " +
                "a1.account_number as from_account_number, " +
                "a2.account_number as to_account_number " +
                "FROM standing_orders so " +
                "JOIN accounts a1 ON so.from_account_id = a1.account_id " +
                "JOIN accounts a2 ON so.to_account_id = a2.account_id " +
                "WHERE (a1.account_number = ? OR a2.account_number = ?) " +
                "AND so.status = 'ACTIVE'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToStandingOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving standing orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Update next execution date after processing
     */
    public boolean updateNextExecutionDate(int standingOrderId, LocalDate nextDate) {
        String sql = "UPDATE standing_orders SET next_execution_date = ?, " +
                "last_execution_date = CURDATE() WHERE standing_order_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(nextDate));
            pstmt.setInt(2, standingOrderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error updating standing order: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cancel a standing order
     */
    public boolean cancelStandingOrder(int standingOrderId) {
        String sql = "UPDATE standing_orders SET status = 'CANCELLED' WHERE standing_order_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, standingOrderId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✓ Standing order cancelled");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error cancelling standing order: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check and complete standing orders that have reached end date
     */
    public int completeExpiredStandingOrders() {
        String sql = "UPDATE standing_orders SET status = 'COMPLETED' " +
                "WHERE status = 'ACTIVE' AND end_date IS NOT NULL AND end_date < CURDATE()";

        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("✗ Error completing expired orders: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get account ID by account number
     */
    public int getAccountId(String accountNumber) {
        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("account_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting account ID: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Map ResultSet to StandingOrderDTO
     */
    private StandingOrderDTO mapResultSetToStandingOrder(ResultSet rs) throws SQLException {
        StandingOrderDTO order = new StandingOrderDTO();
        order.setStandingOrderId(rs.getInt("standing_order_id"));
        order.setFromAccountId(rs.getInt("from_account_id"));
        order.setToAccountId(rs.getInt("to_account_id"));
        order.setFromAccountNumber(rs.getString("from_account_number"));
        order.setToAccountNumber(rs.getString("to_account_number"));
        order.setAmount(rs.getDouble("amount"));
        order.setFrequency(rs.getString("frequency"));
        order.setStartDate(rs.getDate("start_date").toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            order.setEndDate(endDate.toLocalDate());
        }

        order.setNextExecutionDate(rs.getDate("next_execution_date").toLocalDate());

        Date lastExecDate = rs.getDate("last_execution_date");
        if (lastExecDate != null) {
            order.setLastExecutionDate(lastExecDate.toLocalDate());
        }

        order.setDescription(rs.getString("description"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}

// Note: StandingOrderDTO is now in a separate file (dto/StandingOrderDTO.java)