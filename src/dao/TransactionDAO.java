package dao;

// TransactionDAO.java
import config.DatabaseConnection;
import dto.TransactionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction operations
 * Handles all transaction-related database operations
 */
public class TransactionDAO {
    private Connection connection;

    public TransactionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean recordTransaction(String accountNumber, String transactionType,
                                     double amount, double balanceAfter, String description) {

        int accountId = getAccountId(accountNumber);
        if (accountId == -1) {
            System.err.println("✗ Account not found: " + accountNumber);
            return false;
        }

        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                "balance_after, description) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, transactionType.toUpperCase());
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, balanceAfter);
            pstmt.setString(5, description);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error recording transaction: " + e.getMessage());
        }
        return false;
    }

    public boolean recordTransfer(String fromAcc, String toAcc,
                                  double amount, double fromBalance, double toBalance) {

        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        try {
            dbConn.beginTransaction();

            int fromId = getAccountId(fromAcc);
            int toId = getAccountId(toAcc);

            if (fromId == -1 || toId == -1) {
                dbConn.rollback();
                return false;
            }

            String sqlOut = "INSERT INTO transactions " +
                    "(account_id, transaction_type, amount, balance_after, description, reference_account_id) " +
                    "VALUES (?, 'TRANSFER_OUT', ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sqlOut)) {
                pstmt.setInt(1, fromId);
                pstmt.setDouble(2, amount);
                pstmt.setDouble(3, fromBalance);
                pstmt.setString(4, "Transfer to " + toAcc);
                pstmt.setInt(5, toId);
                pstmt.executeUpdate();
            }

            String sqlIn = "INSERT INTO transactions " +
                    "(account_id, transaction_type, amount, balance_after, description, reference_account_id) " +
                    "VALUES (?, 'TRANSFER_IN', ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sqlIn)) {
                pstmt.setInt(1, toId);
                pstmt.setDouble(2, amount);
                pstmt.setDouble(3, toBalance);
                pstmt.setString(4, "Transfer from " + fromAcc);
                pstmt.setInt(5, fromId);
                pstmt.executeUpdate();
            }

            dbConn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("✗ Error recording transfer: " + e.getMessage());
            dbConn.rollback();
        }
        return false;
    }

    public List<TransactionDTO> getTransactionHistory(String accountNumber) {
        List<TransactionDTO> list = new ArrayList<>();

        String sql = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.account_id " +
                "WHERE a.account_number = ? ORDER BY t.transaction_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transactions: " + e.getMessage());
        }
        return list;
    }

    public List<TransactionDTO> getRecentTransactions(int limit) {
        List<TransactionDTO> list = new ArrayList<>();

        String sql = "SELECT t.*, a.account_number, a.account_holder " +
                "FROM transactions t JOIN accounts a ON t.account_id = a.account_id " +
                "ORDER BY t.transaction_date DESC LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransactionDTO dto = mapResultSetToTransaction(rs);
                    dto.setAccountNumber(rs.getString("account_number"));
                    dto.setAccountHolder(rs.getString("account_holder"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving recent transactions: " + e.getMessage());
        }
        return list;
    }

    private int getAccountId(String accountNumber) {
        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("account_id");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching account ID: " + e.getMessage());
        }
        return -1;
    }

    private TransactionDTO mapResultSetToTransaction(ResultSet rs) throws SQLException {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(rs.getInt("transaction_id"));
        dto.setAccountId(rs.getInt("account_id"));
        dto.setTransactionType(rs.getString("transaction_type"));
        dto.setAmount(rs.getDouble("amount"));
        dto.setBalanceAfter(rs.getDouble("balance_after"));
        dto.setTransactionDate(rs.getTimestamp("transaction_date"));
        dto.setDescription(rs.getString("description"));
        return dto;
    }
}
