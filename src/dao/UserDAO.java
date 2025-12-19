package dao;

import config.DatabaseConnection;
import security.SecurityUtil;
import dto.UserDTO;

import java.sql.*;

/**
 * Data Access Object for User operations
 * Implements secure authentication and password handling
 */
public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean registerUser(String username, String password, String email,
                                String fullName, String role) {

        if (!SecurityUtil.isPasswordStrong(password)) {
            System.out.println("✗ " + SecurityUtil.getPasswordStrengthFeedback(password));
            return false;
        }

        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(password, salt);

        String sql = "INSERT INTO users " +
                "(username, password_hash, password_salt, email, full_name, role, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (PreparedStatement pstmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hash);
            pstmt.setString(3, salt);
            pstmt.setString(4, email);
            pstmt.setString(5, fullName);
            pstmt.setString(6, role);

            return pstmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("✗ Username or email already exists");
        } catch (SQLException e) {
            System.err.println("✗ Error registering user: " + e.getMessage());
        }
        return false;
    }

    public UserDTO authenticateUser(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND status = 'ACTIVE'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("password_salt");

                if (SecurityUtil.verifyPassword(password, salt, storedHash)) {
                    updateLastLogin(rs.getInt("user_id"));
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Authentication error: " + e.getMessage());
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {

        UserDTO user = authenticateUser(username, oldPassword);
        if (user == null) return false;

        if (!SecurityUtil.isPasswordStrong(newPassword)) {
            System.out.println("✗ " + SecurityUtil.getPasswordStrengthFeedback(newPassword));
            return false;
        }

        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(newPassword, salt);

        String sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hash);
            pstmt.setString(2, salt);
            pstmt.setInt(3, user.getUserId());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error changing password: " + e.getMessage());
        }
        return false;
    }

    public UserDTO getUserByUsername(String username) {

        String sql = "SELECT user_id, username, email, full_name, role, status, " +
                "created_date, last_login FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public boolean lockUserAccount(String username) {

        String sql = "UPDATE users SET status = 'LOCKED' WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Error locking account: " + e.getMessage());
        }
        return false;
    }

    private void updateLastLogin(int userId) {

        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("✗ Error updating last login: " + e.getMessage());
        }
    }

    private UserDTO mapUser(ResultSet rs) throws SQLException {

        UserDTO user = new UserDTO();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedDate(rs.getTimestamp("created_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
