package com.hust.soict.aims.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading application configuration
 * Reads from application.properties file
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties from application.properties file
     */
    private static void loadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                System.err.println("Unable to find application.properties");
                return;
            }
            
            properties.load(input);
            
        } catch (IOException e) {
            System.err.println("Error loading application.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value, or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property value with default
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get VietQR username
     */
    public static String getVietQRUsername() {
        return getProperty("vietqr.username", "customer-vietqrtest-user2468");
    }
    
    /**
     * Get VietQR password
     */
    public static String getVietQRPassword() {
        return getProperty("vietqr.password", "customer-vietqrtest-user2468=");
    }
    
    /**
     * Get VietQR bank account number (for receiving payments)
     * Field name: bankAccount (as per VietQR API docs)
     */
    public static String getVietQRBankAccount() {
        return getProperty("vietqr.accountNo", "9704198526191432198");
    }
    
    /**
     * Get VietQR account holder name (no diacritics)
     * Field name: userBankName (as per VietQR API docs)
     */
    public static String getVietQRUserBankName() {
        return getProperty("vietqr.accountName", "NGUYEN VAN A");
    }
    
    /**
     * Get VietQR bank code (e.g., "MB", "970415")
     * Field name: bankCode (as per VietQR API docs)
     */
    public static String getVietQRBankCode() {
        return getProperty("vietqr.bankCode", "970415");
    }
}

