package com.hust.soict.aims.dao;

import com.hust.soict.aims.utils.PasswordHasher;

import java.sql.*;

/**
 * Database Initializer
 * Responsible for creating database schema and initializing default data
 */
public class DatabaseInitializer {
    private static final String URL = BaseDAO.URL;
    
    /**
     * Initialize database schema and default data
     */
    public static void initialize() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                createTables(st);
                initializeDefaultData(conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    
    /**
     * Create all database tables
     */
    private static void createTables(Statement st) throws SQLException {
        // =======================
        // Users & Roles
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS Users (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "password_hash VARCHAR(255) NOT NULL, " +
            "email VARCHAR(100) UNIQUE NOT NULL, " +
            "full_name VARCHAR(100) NOT NULL, " +
            "phone_number VARCHAR(20), " +
            "status VARCHAR(20) DEFAULT 'active', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        
        st.execute("CREATE TABLE IF NOT EXISTS Role (" +
            "role_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "role_name VARCHAR(50) UNIQUE NOT NULL)");
        
        st.execute("CREATE TABLE IF NOT EXISTS UserRole (" +
            "user_id INTEGER NOT NULL, " +
            "role_id INTEGER NOT NULL, " +
            "assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (user_id, role_id), " +
            "FOREIGN KEY (user_id) REFERENCES Users(user_id), " +
            "FOREIGN KEY (role_id) REFERENCES Role(role_id))");
        
        // =======================
        // Media Products
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS Media (" +
            "media_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "category VARCHAR(50) NOT NULL, " +
            "barcode VARCHAR(50) UNIQUE NOT NULL, " +
            "title VARCHAR(100) NOT NULL, " +
            "description VARCHAR(200), " +
            "price REAL NOT NULL, " +
            "value REAL NOT NULL, " +
            "quantity INTEGER DEFAULT 0, " +
            "weight REAL NOT NULL, " +
            "height REAL, " +
            "width REAL, " +
            "length REAL, " +
            "condition VARCHAR(20) DEFAULT 'new', " +
            "image_url VARCHAR(200) NOT NULL, " +
            "status VARCHAR(20) DEFAULT 'active', " +
            "created_by INTEGER, " +
            "updated_by INTEGER, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (created_by) REFERENCES Users(user_id), " +
            "FOREIGN KEY (updated_by) REFERENCES Users(user_id))");
        
        // =======================
        // Product Type Details
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS Book (" +
            "media_id INTEGER PRIMARY KEY, " +
            "author VARCHAR(100), " +
            "cover_type VARCHAR(50), " +
            "publisher VARCHAR(100), " +
            "publish_date DATE, " +
            "number_of_page INTEGER, " +
            "language VARCHAR(50), " +
            "book_category VARCHAR(50), " +
            "genre VARCHAR(50), " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS Newspaper (" +
            "media_id INTEGER PRIMARY KEY, " +
            "editor_in_chief VARCHAR(100), " +
            "publisher VARCHAR(100), " +
            "publish_date DATE, " +
            "issue_number VARCHAR(20), " +
            "publication_frequency VARCHAR(50), " +
            "issn VARCHAR(20), " +
            "language VARCHAR(50), " +
            "sections VARCHAR(200), " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS CD (" +
            "media_id INTEGER PRIMARY KEY, " +
            "artist VARCHAR(100), " +
            "record_label VARCHAR(100), " +
            "music_type VARCHAR(50), " +
            "release_date DATE, " +
            "genre VARCHAR(50), " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS Track (" +
            "track_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "media_id INTEGER NOT NULL, " +
            "title VARCHAR(100), " +
            "length INTEGER, " +
            "track_number INTEGER, " +
            "FOREIGN KEY (media_id) REFERENCES CD(media_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS DVD (" +
            "media_id INTEGER PRIMARY KEY, " +
            "disc_type VARCHAR(50), " +
            "director VARCHAR(100), " +
            "runtime INTEGER, " +
            "studio VARCHAR(100), " +
            "language VARCHAR(50), " +
            "subtitle VARCHAR(100), " +
            "release_date DATE, " +
            "genre VARCHAR(50), " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id))");
        
        // =======================
        // Product History
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS ProductHistory (" +
            "history_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "media_id INTEGER, " +
            "user_id INTEGER, " +
            "action_type VARCHAR(20), " +
            "reason VARCHAR(200), " +
            "action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id), " +
            "FOREIGN KEY (user_id) REFERENCES Users(user_id))");
        
        // =======================
        // Orders & Delivery
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS Orders (" +
            "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "status VARCHAR(30) DEFAULT 'pending', " +
            "processed_by INTEGER, " +
            "processed_at TIMESTAMP, " +
            "reject_reason VARCHAR(200), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (processed_by) REFERENCES Users(user_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS DeliveryInfo (" +
            "order_id INTEGER PRIMARY KEY, " +
            "recipient_name VARCHAR(100), " +
            "phone_number VARCHAR(20), " +
            "email VARCHAR(100), " +
            "delivery_address VARCHAR(200), " +
            "city VARCHAR(50), " +
            "instructions VARCHAR(200), " +
            "FOREIGN KEY (order_id) REFERENCES Orders(order_id))");
        
        st.execute("CREATE TABLE IF NOT EXISTS OrderMedia (" +
            "order_id INTEGER NOT NULL, " +
            "media_id INTEGER NOT NULL, " +
            "quantity INTEGER, " +
            "price REAL, " +
            "PRIMARY KEY (order_id, media_id), " +
            "FOREIGN KEY (order_id) REFERENCES Orders(order_id), " +
            "FOREIGN KEY (media_id) REFERENCES Media(media_id))");
        
        // =======================
        // Payment & Invoice
        // =======================
        st.execute("CREATE TABLE IF NOT EXISTS PaymentTransaction (" +
            "payment_transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "amount REAL, " +
            "method_type VARCHAR(50), " +
            "transaction_no VARCHAR(100), " +
            "transaction_content VARCHAR(200), " +
            "pay_date TIMESTAMP, " +
            "bank_code VARCHAR(50), " +
            "bank_transaction_no VARCHAR(100), " +
            "card_type VARCHAR(50))");
        
        st.execute("CREATE TABLE IF NOT EXISTS Invoice (" +
            "invoice_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "order_id INTEGER UNIQUE, " +
            "payment_transaction_id INTEGER UNIQUE, " +
            "product_total REAL, " +
            "vat_amount REAL, " +
            "shipping_fee REAL, " +
            "total_amount REAL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (order_id) REFERENCES Orders(order_id), " +
            "FOREIGN KEY (payment_transaction_id) REFERENCES PaymentTransaction(payment_transaction_id))");
    }
    
    /**
     * Initialize default roles and users
     */
    private static void initializeDefaultData(Connection conn) throws SQLException {
        // Initialize default roles
        try (Statement st = conn.createStatement()) {
            st.execute("INSERT OR IGNORE INTO Role (role_name) VALUES ('administrator')");
            st.execute("INSERT OR IGNORE INTO Role (role_name) VALUES ('product_manager')");
        }
        
        // Create default manager user if not exists
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO Users (username, password_hash, email, full_name, status) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, "manager");
            ps.setString(2, PasswordHasher.hashPassword("manager123"));
            ps.setString(3, "manager@aims.com");
            ps.setString(4, "Manager User");
            ps.setString(5, "active");
            ps.executeUpdate();
        }
        
        // Create default manager user with email huyhoang2001037a1@gmail.com
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO Users (username, password_hash, email, full_name, status) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, "huyhoang");
            ps.setString(2, PasswordHasher.hashPassword("Test@123"));
            ps.setString(3, "huyhoang2001037a1@gmail.com");
            ps.setString(4, "Huy Hoang");
            ps.setString(5, "active");
            ps.executeUpdate();
        }
        
        // Assign product_manager role to manager users
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO UserRole (user_id, role_id) " +
            "SELECT u.user_id, r.role_id FROM Users u, Role r " +
            "WHERE (u.username = 'manager' OR u.email = 'huyhoang2001037a1@gmail.com') AND r.role_name = 'product_manager'")) {
            ps.executeUpdate();
        }
    }
    
}
