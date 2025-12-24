package com.hust.soict.aims.controls;

import com.hust.soict.aims.utils.PasswordHasher;
import java.sql.*;

/**
 * Authentication controller for manager login
 */
public class AuthController {
    private static final String DB_FILE = "aims.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticate(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            return false;
        }
        
        String sql = "SELECT u.password_hash, r.role_name " +
                     "FROM Users u " +
                     "LEFT JOIN UserRole ur ON u.user_id = ur.user_id " +
                     "LEFT JOIN Role r ON ur.role_id = r.role_id " +
                     "WHERE u.email = ? AND u.status = 'active'";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String roleName = rs.getString("role_name");
                    
                    // Check if user has manager role (product_manager or administrator)
                    if (roleName == null || (!roleName.equals("product_manager") && !roleName.equals("administrator"))) {
                        return false;
                    }
                    
                    // Verify password
                    return PasswordHasher.verifyPassword(password, storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if email is valid manager email
     * @param email Email to check
     * @return true if valid manager email
     */
    public static boolean isValidManagerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM Users u " +
                     "LEFT JOIN UserRole ur ON u.user_id = ur.user_id " +
                     "LEFT JOIN Role r ON ur.role_id = r.role_id " +
                     "WHERE u.email = ? AND u.status = 'active' " +
                     "AND (r.role_name = 'product_manager' OR r.role_name = 'administrator')";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
