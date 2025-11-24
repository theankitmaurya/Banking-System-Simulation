package dao;

import config.DatabaseConnection;
import dto.AccountDTO;
import model.Account;
import model.CheckingAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection connection;

    public AccountDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, account_holder, account_type, " +
                "balance, interest_rate, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountHolder());
            pstmt.setString(3, account.getAccountType().toUpperCase().replace(" ", "_"));
            pstmt.setDouble(4, account.getBalance());
            pstmt.setDouble(5, account.getInterestRate());
            pstmt.setString(6, "ACTIVE");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int accountId = generatedKeys.getInt(1);
                        createAccountSpecificEntry(accountId, account);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }
        return false;
    }

    private void createAccountSpecificEntry(int accountId, Account account) throws SQLException {
        if (account instanceof SavingsAccount) {
            String sql = "INSERT INTO savings_accounts (account_id, minimum_balance) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, accountId);
                pstmt.setDouble(2, SavingsAccount.getMinimumBalance());
                pstmt.executeUpdate();
            }
        } else if (account instanceof CheckingAccount) {
            String sql = "INSERT INTO checking_accounts (account_id, overdraft_limit) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, accountId);
                pstmt.setDouble(2, ((CheckingAccount) account).getOverdraftLimit());
                pstmt.executeUpdate();
            }
        } else if (account instanceof FixedDepositAccount) {
            FixedDepositAccount fd = (FixedDepositAccount) account;
            String sql = "INSERT INTO fixed_deposit_accounts (account_id, term_months, maturity_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, accountId);
                pstmt.setInt(2, fd.getTermMonths());
                pstmt.setDate(3, Date.valueOf(fd.getMaturityDate().toLocalDate()));
                pstmt.executeUpdate();
            }
        }
    }

    public AccountDTO getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }

    public List<AccountDTO> getAllAccounts() {
        List<AccountDTO> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY created_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }
        return accounts;
    }

    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
        return false;
    }

    public List<AccountDTO> getAccountsByHolder(String holderName) {
        List<AccountDTO> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE account_holder LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + holderName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching accounts: " + e.getMessage());
        }
        return accounts;
    }

    private AccountDTO mapResultSetToAccount(ResultSet rs) throws SQLException {
        AccountDTO account = new AccountDTO();
        account.setAccountId(rs.getInt("account_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountHolder(rs.getString("account_holder"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));
        account.setInterestRate(rs.getDouble("interest_rate"));
        account.setCreatedDate(rs.getTimestamp("created_date"));
        account.setStatus(rs.getString("status"));
        return account;
    }
}
