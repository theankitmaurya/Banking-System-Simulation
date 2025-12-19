package dto;

import java.sql.Timestamp;

/**
 * Data Transfer Object for User
 */
public class UserDTO {

    private int userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private Timestamp createdDate;
    private Timestamp lastLogin;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return String.format(
                "User[%s] | %s | %s | Role: %s | Status: %s",
                username, fullName, email, role, status
        );
    }
}
