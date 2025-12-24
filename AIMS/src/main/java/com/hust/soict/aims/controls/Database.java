package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.ImageUtils;
import com.hust.soict.aims.utils.PasswordHasher;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String DB_FILE = "aims.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    /**
     * Functional interface for setting PreparedStatement parameters
     */
    @FunctionalInterface
    private interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
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
                
                // =======================
                // Legacy products table (for backward compatibility)
                // =======================
                st.execute("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, title TEXT, originalValue REAL, currentPrice REAL, weight REAL, dimension TEXT, description TEXT, extra TEXT)");
                if (!hasColumn(conn, "products", "stock")) {
                    st.execute("ALTER TABLE products ADD COLUMN stock INTEGER DEFAULT 10");
                }
                if (!hasColumn(conn, "products", "barcode")) {
                    st.execute("ALTER TABLE products ADD COLUMN barcode TEXT");
                }
                if (!hasColumn(conn, "products", "imagePath")) {
                    st.execute("ALTER TABLE products ADD COLUMN imagePath TEXT");
                }
                
                // Initialize default roles
                st.execute("INSERT OR IGNORE INTO Role (role_name) VALUES ('administrator')");
                st.execute("INSERT OR IGNORE INTO Role (role_name) VALUES ('product_manager')");
                
                // Create default manager user if not exists
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO Users (username, password_hash, email, full_name, status) VALUES (?, ?, ?, ?, ?)")) {
                    // Simple hash for "manager123" - in production, use proper hashing like BCrypt
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
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
            // Migrate data from products to Media if Media is empty
            migrateProductsToMedia(conn);
            
            // Seed some data if Media is empty
            try (Statement st = conn.createStatement(); 
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedMedia(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
                }
            }

    /**
     * Migrate data from legacy products table to new Media schema
     */
    private static void migrateProductsToMedia(Connection conn) {
        try {
            // Check if Media table is empty and products table has data
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // Already migrated
                }
            }
            
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
                if (!rs.next() || rs.getInt(1) == 0) {
                    return; // No data to migrate
                }
            }
            
            // Get manager user_id for created_by
            int managerUserId = 1;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                if (rs.next()) {
                    managerUserId = rs.getInt(1);
                }
            }
            
            // Migrate products to Media
            String selectProducts = "SELECT id, type, title, originalValue, currentPrice, weight, dimension, description, extra, barcode, imagePath FROM products";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(selectProducts)) {
                
                conn.setAutoCommit(false);
                while (rs.next()) {
                    long oldId = rs.getLong("id");
                    String type = rs.getString("type");
                    String title = rs.getString("title");
                    double originalValue = rs.getDouble("originalValue");
                    double currentPrice = rs.getDouble("currentPrice");
                    double weight = rs.getDouble("weight");
                    String dimension = rs.getString("dimension");
                    String description = rs.getString("description");
                    String extra = rs.getString("extra");
                    String barcode = rs.getString("barcode");
                    String imagePath = rs.getString("imagePath");
                    
                    // Parse dimension to height, width, length if possible
                    Double height = null, width = null, length = null;
                    if (dimension != null && !dimension.isEmpty()) {
                        // Try to parse "15x20cm" or similar
                        String[] parts = dimension.replaceAll("[^0-9xX.]", "").split("[xX]");
                        if (parts.length >= 2) {
                            try {
                                width = Double.parseDouble(parts[0]);
                                height = Double.parseDouble(parts[1]);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    
                    // Insert into Media
                    String insertMedia = "INSERT INTO Media (category, barcode, title, description, price, value, quantity, weight, height, width, length, image_url, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertMedia, Statement.RETURN_GENERATED_KEYS)) {
                        // Normalize category to lowercase for case-insensitive consistency
                        ps.setString(1, type != null ? type.toLowerCase() : null);
                        ps.setString(2, barcode != null ? barcode : "LEGACY" + oldId);
                        ps.setString(3, title);
                        ps.setString(4, description);
                        ps.setDouble(5, currentPrice);
                        ps.setDouble(6, originalValue);
                        ps.setInt(7, 10); // Default quantity
                        ps.setDouble(8, weight);
                        if (height != null) ps.setDouble(9, height);
                        else ps.setNull(9, Types.REAL);
                        if (width != null) ps.setDouble(10, width);
                        else ps.setNull(10, Types.REAL);
                        if (length != null) ps.setDouble(11, length);
                        else ps.setNull(11, Types.REAL);
                        ps.setString(12, imagePath != null ? imagePath : ImageUtils.getProductImagePathAlways(oldId));
                        ps.setInt(13, managerUserId);
                        ps.setInt(14, managerUserId);
                        ps.executeUpdate();
                        
                        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                long newMediaId = generatedKeys.getLong(1);
                                
                                // Insert into type-specific tables
                                Map<String, String> extraMap = parseExtra(extra);
                                switch (type) {
                                    case "book":
                                        insertBook(conn, newMediaId, extraMap);
                                        break;
                                    case "newspaper":
                                        insertNewspaper(conn, newMediaId, extraMap);
                                        break;
                                    case "cd":
                                        insertCD(conn, newMediaId, extraMap);
                                        break;
                                    case "dvd":
                                        insertDVD(conn, newMediaId, extraMap);
                                        break;
                                }
                            }
                        }
                    }
                }
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    
    private static void insertBook(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO Book (media_id, author, cover_type, publisher, publish_date, number_of_page, language, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("author", null));
            ps.setString(3, extra.getOrDefault("coverType", null));
            ps.setString(4, extra.getOrDefault("publisher", null));
            ps.setString(5, extra.getOrDefault("publicationDate", null));
            String pages = extra.get("numberOfPages");
            if (pages != null) {
                try {
                    ps.setInt(6, Integer.parseInt(pages));
                } catch (NumberFormatException e) {
                    ps.setNull(6, Types.INTEGER);
                }
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, extra.getOrDefault("language", null));
            ps.setString(8, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertNewspaper(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO Newspaper (media_id, editor_in_chief, publisher, publish_date, issue_number, publication_frequency, issn, language, sections) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("editorInChief", null));
            ps.setString(3, extra.getOrDefault("publisher", null));
            ps.setString(4, extra.getOrDefault("publicationDate", null));
            ps.setString(5, extra.getOrDefault("issueNumber", null));
            ps.setString(6, extra.getOrDefault("publicationFrequency", null));
            ps.setString(7, extra.getOrDefault("issn", null));
            ps.setString(8, extra.getOrDefault("language", null));
            ps.setString(9, extra.getOrDefault("sections", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertCD(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO CD (media_id, artist, record_label, music_type, release_date, genre) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("artist", null));
            ps.setString(3, extra.getOrDefault("recordLabel", null));
            ps.setString(4, extra.getOrDefault("album", null)); // Using album as music_type
            ps.setString(5, extra.getOrDefault("releaseDate", null));
            ps.setString(6, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertDVD(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO DVD (media_id, disc_type, director, runtime, studio, language, subtitle, release_date, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("discType", null));
            ps.setString(3, extra.getOrDefault("director", null));
            String runtime = extra.get("runtime");
            if (runtime != null) {
                // Try to extract number from "120min" or similar
                runtime = runtime.replaceAll("[^0-9]", "");
                if (!runtime.isEmpty()) {
                    try {
                        ps.setInt(4, Integer.parseInt(runtime));
                    } catch (NumberFormatException e) {
                        ps.setNull(4, Types.INTEGER);
                    }
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, extra.getOrDefault("studio", null));
            ps.setString(6, extra.getOrDefault("language", null));
            ps.setString(7, extra.getOrDefault("subtitles", null));
            ps.setString(8, extra.getOrDefault("releaseDate", null));
            ps.setString(9, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }

    private static boolean hasColumn(Connection conn, String table, String column) throws SQLException {
        String q = "PRAGMA table_info(" + table + ")";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (column.equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }

    /**
     * Seed Media table with sample data
     */
    private static void seedMedia(Connection conn) throws SQLException {
        // Get manager user_id
        int managerUserId = 1;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
            if (rs.next()) {
                managerUserId = rs.getInt(1);
            }
        }
        
        // Seed books
            for (int i = 1; i <= 15; i++) {
            long mediaId = insertMedia(conn, "Book", "BOOK" + String.format("%06d", i), 
                "Book title " + i, "A great book.", 80.0 + i, 100.0 + i, 10, 
                0.5 + i * 0.01, 20.0, 15.0, null, "new", 
                ImageUtils.getProductImagePathAlways(i), managerUserId);
            
            insertBook(conn, mediaId, Map.of(
                "author", "Author " + i,
                "coverType", "paperback",
                "publisher", "Pub " + i,
                "publicationDate", "2020-01-0" + ((i%9)+1),
                "numberOfPages", String.valueOf(200 + i * 10),
                "language", "English",
                "genre", "Fiction"
            ));
            }

        // Seed newspapers
            for (int i = 1; i <= 10; i++) {
            long mediaId = insertMedia(conn, "Newspaper", "NEWS" + String.format("%06d", i),
                "Newspaper " + i, "Daily news.", 3.0 + i, 5.0 + i, 10,
                0.2, 40.0, 30.0, null, "new",
                ImageUtils.getProductImagePathAlways(15 + i), managerUserId);
            
            insertNewspaper(conn, mediaId, Map.of(
                "editorInChief", "Editor " + i,
                "publisher", "NewsPub",
                "publicationDate", "2025-10-0" + ((i%9)+1),
                "issueNumber", String.valueOf(i),
                "publicationFrequency", "daily",
                "issn", "1234-" + i,
                "language", "Vietnamese",
                "sections", "News, Sports, Entertainment"
            ));
            }

        // Seed CDs
            for (int i = 1; i <= 12; i++) {
            long mediaId = insertMedia(conn, "CD", "CD" + String.format("%06d", i),
                "Album " + i, "Music album.", 12.0 + i, 15.0 + i, 10,
                0.1, 12.0, 12.0, null, "new",
                ImageUtils.getProductImagePathAlways(25 + i), managerUserId);
            
            insertCD(conn, mediaId, Map.of(
                "artist", "Artist " + i,
                "recordLabel", "Label " + i,
                "album", "Album " + i,
                "releaseDate", "2019-05-0" + ((i%9)+1),
                "genre", "Pop"
            ));
            }

        // Seed DVDs
            for (int i = 1; i <= 13; i++) {
            long mediaId = insertMedia(conn, "DVD", "DVD" + String.format("%06d", i),
                "Movie " + i, "A movie.", 20.0 + i, 25.0 + i, 10,
                0.2, 19.0, 14.0, null, "new",
                ImageUtils.getProductImagePathAlways(37 + i), managerUserId);
            
            insertDVD(conn, mediaId, Map.of(
                "discType", "Blu-ray",
                "director", "Director " + i,
                "runtime", "120",
                "studio", "Studio " + i,
                "language", "English",
                "subtitles", "Vietnamese",
                "releaseDate", "2018-0" + ((i%9)+1),
                "genre", "Action"
            ));
        }
    }
    
    private static long insertMedia(Connection conn, String category, String barcode, String title,
                                   String description, double price, double value, int quantity,
                                   double weight, Double width, Double height, Double length,
                                   String condition, String imageUrl, int createdBy) throws SQLException {
        String sql = "INSERT INTO Media (category, barcode, title, description, price, value, quantity, weight, width, height, length, condition, image_url, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Normalize category to lowercase for case-insensitive consistency
            ps.setString(1, category != null ? category.toLowerCase() : null);
            ps.setString(2, barcode);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setDouble(5, price);
            ps.setDouble(6, value);
            ps.setInt(7, quantity);
            ps.setDouble(8, weight);
            if (width != null) ps.setDouble(9, width);
            else ps.setNull(9, Types.REAL);
            if (height != null) ps.setDouble(10, height);
            else ps.setNull(10, Types.REAL);
            if (length != null) ps.setDouble(11, length);
            else ps.setNull(11, Types.REAL);
            ps.setString(12, condition);
            ps.setString(13, imageUrl);
            ps.setInt(14, createdBy);
            ps.setInt(15, createdBy);
                ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
            }
        }
        }
        return -1;
    }

    // =======================
    // Legacy methods for backward compatibility
    // These methods work with the old products table
    // =======================

    public static int countProducts() {
        // Try Media first, fallback to products
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) return count;
            }
        } catch (SQLException e) {}
        
        // Fallback to products
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getStock(long productId) {
        // Try Media first
        String q = "SELECT quantity FROM Media WHERE media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("quantity");
            }
        } catch (SQLException e) {}
        
        // Fallback to products
        q = "SELECT stock FROM products WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static boolean reduceStock(long productId, int amount) {
        // Try Media first
        int current = getStock(productId);
        if (current < amount) return false;
        
        String u = "UPDATE Media SET quantity = quantity - ? WHERE media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, amount);
            ps.setLong(2, productId);
            int affected = ps.executeUpdate();
            if (affected > 0) return true;
        } catch (SQLException e) {}
        
        // Fallback to products
        u = "UPDATE products SET stock = stock - ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, amount);
            ps.setLong(2, productId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void setStock(long productId, int stock) {
        // Try Media first
        String u = "UPDATE Media SET quantity = ? WHERE media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, stock);
            ps.setLong(2, productId);
            if (ps.executeUpdate() > 0) return;
        } catch (SQLException e) {}
        
        // Fallback to products
        u = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, stock);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Continue with legacy queryProducts method and other methods...
    // For now, keeping the old methods for backward compatibility

    public static List<Product> getProducts(int offset, int limit) {
        // Try to get from Media first
        List<Product> mediaProducts = queryMediaProducts(offset, limit);
        if (!mediaProducts.isEmpty()) {
            return mediaProducts;
        }
        
        // Fallback to products
        return queryProducts("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra,barcode,imagePath FROM products ORDER BY id LIMIT ? OFFSET ?", 
                            stmt -> {
                                stmt.setInt(1, limit);
                                stmt.setInt(2, offset);
                            });
    }
    
    /**
     * Query products from Media table
     */
    private static List<Product> queryMediaProducts(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m " +
                     "WHERE m.status = 'active' " +
                     "ORDER BY m.media_id LIMIT ? OFFSET ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Map Media row to Product object
     */
    private static Product mapMediaToProduct(Connection conn, ResultSet rs) throws SQLException {
        long mediaId = rs.getLong("media_id");
        String category = rs.getString("category");
        String barcode = rs.getString("barcode");
        String title = rs.getString("title");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        double value = rs.getDouble("value");
        int quantity = rs.getInt("quantity");
        double weight = rs.getDouble("weight");
        Double width = rs.getObject("width") != null ? rs.getDouble("width") : null;
        Double height = rs.getObject("height") != null ? rs.getDouble("height") : null;
        Double length = rs.getObject("length") != null ? rs.getDouble("length") : null;
        String condition = rs.getString("condition");
        String status = rs.getString("status");
        String imageUrl = rs.getString("image_url");
        Integer createdBy = rs.getObject("created_by") != null ? rs.getInt("created_by") : null;
        Integer updatedBy = rs.getObject("updated_by") != null ? rs.getInt("updated_by") : null;
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        
        // Build dimension string
        String dimension = "";
        if (width != null && height != null) {
            dimension = width.intValue() + "x" + height.intValue() + "cm";
        }
        
        Product p = null;
        switch (category.toLowerCase()) {
            case "book": {
                Book b = loadBookDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = b;
                break;
            }
            case "newspaper": {
                Newspaper n = loadNewspaperDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = n;
                break;
            }
            case "cd": {
                CD c = loadCDDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = c;
                break;
            }
            case "dvd": {
                DVD d = loadDVDDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = d;
                break;
            }
            default: {
                p = new Product(mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                break;
            }
        }
        
        // Set common fields
        p.setCategory(category);
        p.setQuantity(quantity);
        p.setWidth(width);
        p.setHeight(height);
        p.setLength(length);
        if (condition != null) p.setCondition(condition);
        if (status != null) p.setStatus(status);
        p.setCreatedBy(createdBy);
        p.setUpdatedBy(updatedBy);
        p.setCreatedAt(createdAt);
        p.setUpdatedAt(updatedAt);
        
        return p;
    }
    
    private static Book loadBookDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre FROM Book WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book(id, title, value, price, weight, dimension, description,
                        rs.getString("author"), rs.getString("cover_type"), rs.getString("publisher"), 
                        rs.getString("publish_date"));
                    b.setNumberOfPages(rs.getObject("number_of_page") != null ? rs.getInt("number_of_page") : null);
                    b.setLanguage(rs.getString("language"));
                    b.setBookCategory(rs.getString("book_category"));
                    b.setGenre(rs.getString("genre"));
                    b.setBarcode(barcode);
                    b.setImagePath(imageUrl);
                    return b;
                }
            }
        }
        return new Book(id, title, value, price, weight, dimension, description, "", "", "", "");
    }
    
    private static Newspaper loadNewspaperDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT editor_in_chief, publisher, publish_date, issue_number, publication_frequency, issn, language, sections FROM Newspaper WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Newspaper n = new Newspaper(id, title, value, price, weight, dimension, description,
                        rs.getString("editor_in_chief"), rs.getString("publisher"), rs.getString("publish_date"));
                    n.setIssueNumber(rs.getString("issue_number"));
                    n.setPublicationFrequency(rs.getString("publication_frequency"));
                    n.setIssn(rs.getString("issn"));
                    n.setLanguage(rs.getString("language"));
                    n.setSections(rs.getString("sections"));
                    n.setBarcode(barcode);
                    n.setImagePath(imageUrl);
                    return n;
                }
            }
        }
        return new Newspaper(id, title, value, price, weight, dimension, description, "", "", "");
    }
    
    private static CD loadCDDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT artist, record_label, music_type, release_date, genre FROM CD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CD c = new CD(id, title, value, price, weight, dimension, description,
                        rs.getString("music_type"), rs.getString("artist"), rs.getString("record_label"));
                    c.setGenre(rs.getString("genre"));
                    c.setReleaseDate(rs.getString("release_date"));
                    c.setBarcode(barcode);
                    c.setImagePath(imageUrl);
                    return c;
                }
            }
        }
        return new CD(id, title, value, price, weight, dimension, description, "", "", "");
    }
    
    private static DVD loadDVDDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT disc_type, director, runtime, studio, language, subtitle, release_date, genre FROM DVD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DVD d = new DVD(id, title, value, price, weight, dimension, description,
                        rs.getString("disc_type"), rs.getString("director"));
                    d.setRuntime(rs.getObject("runtime") != null ? rs.getInt("runtime") : null);
                    d.setStudio(rs.getString("studio"));
                    d.setLanguage(rs.getString("language"));
                    d.setSubtitles(rs.getString("subtitle"));
                    d.setReleaseDate(rs.getString("release_date"));
                    d.setGenre(rs.getString("genre"));
                    d.setBarcode(barcode);
                    d.setImagePath(imageUrl);
                    return d;
                }
            }
        }
        return new DVD(id, title, value, price, weight, dimension, description, "", "");
    }
    
    /**
     * Search products by title (case-insensitive)
     */
    public static List<Product> searchProducts(String searchTerm, int offset, int limit) {
        // Try Media first
        List<Product> results = searchMediaProducts(searchTerm, null, null, null, offset, limit);
        if (!results.isEmpty()) {
            return results;
        }
        
        // Fallback to products
        return queryProducts("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra,barcode,imagePath FROM products WHERE LOWER(title) LIKE ? ORDER BY id LIMIT ? OFFSET ?",
                            stmt -> {
                                stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
                                stmt.setInt(2, limit);
                                stmt.setInt(3, offset);
                            });
    }
    
    /**
     * Search Media products with filters
     */
    private static List<Product> searchMediaProducts(String searchTerm, String category, Double minPrice, Double maxPrice, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE m.status = 'active'");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(m.category) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND m.price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND m.price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY m.media_id LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Search and filter products with multiple criteria
     */
    public static List<Product> searchProductsWithFilters(String searchTerm, String category, Double minPrice, Double maxPrice, int offset, int limit) {
        // Try Media first
        List<Product> results = searchMediaProducts(searchTerm, category, minPrice, maxPrice, offset, limit);
        if (!results.isEmpty() || hasMediaData()) {
            return results;
        }
        
        // Fallback to products
        StringBuilder sql = new StringBuilder("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra,barcode,imagePath FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(type) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND currentPrice >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND currentPrice <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY id LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        return queryProducts(sql.toString(), stmt -> {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
                            });
    }
    
    /**
     * Count products matching search term
     */
    public static int countSearchResults(String searchTerm) {
        // Try Media first
        String q = "SELECT COUNT(*) FROM Media WHERE status = 'active' AND LOWER(title) LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) return count;
                }
            }
        } catch (SQLException e) {}
        
        // Fallback to products
        q = "SELECT COUNT(*) FROM products WHERE LOWER(title) LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    /**
     * Count products matching filter criteria
     */
    public static int countFilteredResults(String searchTerm, String category, Double minPrice, Double maxPrice) {
        // Try Media first
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Media WHERE status = 'active'");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(category) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0 || hasMediaData()) return count;
                }
            }
        } catch (SQLException e) {}
        
        // Fallback to products
        sql = new StringBuilder("SELECT COUNT(*) FROM products WHERE 1=1");
        params.clear();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(type) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND currentPrice >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND currentPrice <= ?");
            params.add(maxPrice);
        }
        
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    /**
     * Check if Media table has data
     */
    private static boolean hasMediaData() {
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }
    
    /**
     * Legacy queryProducts method for backward compatibility
     */
    private static List<Product> queryProducts(String sql, PreparedStatementSetter setter) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String type = rs.getString("type");
                    String title = rs.getString("title");
                    double original = rs.getDouble("originalValue");
                    double current = rs.getDouble("currentPrice");
                    double weight = rs.getDouble("weight");
                    String dimension = rs.getString("dimension");
                    String desc = rs.getString("description");
                    String extra = rs.getString("extra");
                    String barcode = rs.getString("barcode");
                    String imagePath = rs.getString("imagePath");
                    
                    if (imagePath == null || imagePath.isEmpty()) {
                        imagePath = ImageUtils.getProductImagePathAlways(id);
                    }

                    Map<String,String> m = parseExtra(extra);

                    Product p = null;
                    switch (type) {
                        case "book": {
                            Book b = new Book(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("author",""), m.getOrDefault("coverType",""), m.getOrDefault("publisher",""), m.getOrDefault("publicationDate",""));
                            if (m.containsKey("numberOfPages")) try { b.setNumberOfPages(Integer.parseInt(m.get("numberOfPages"))); } catch (Exception ignored) {}
                            b.setLanguage(m.getOrDefault("language", ""));
                            b.setGenre(m.getOrDefault("genre",""));
                            b.setBarcode(barcode);
                            b.setImagePath(imagePath);
                            p = b; break;
                        }
                        case "newspaper": {
                            Newspaper n = new Newspaper(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("editorInChief",""), m.getOrDefault("publisher",""), m.getOrDefault("publicationDate",""));
                            n.setIssueNumber(m.getOrDefault("issueNumber",""));
                            n.setPublicationFrequency(m.getOrDefault("publicationFrequency",""));
                            n.setIssn(m.getOrDefault("issn",""));
                            n.setLanguage(m.getOrDefault("language",""));
                            n.setSections(m.getOrDefault("sections",""));
                            n.setBarcode(barcode);
                            n.setImagePath(imagePath);
                            p = n; break;
                        }
                        case "cd": {
                            CD c = new CD(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("album",""), m.getOrDefault("artist",""), m.getOrDefault("recordLabel",""));
                            c.setGenre(m.getOrDefault("genre",""));
                            c.setReleaseDate(m.getOrDefault("releaseDate",""));
                            if (m.containsKey("trackList")) c.setTrackList(Arrays.asList(m.get("trackList").split("\\|")));
                            c.setBarcode(barcode);
                            c.setImagePath(imagePath);
                            p = c; break;
                        }
                        case "dvd": {
                            DVD d = new DVD(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("discType",""), m.getOrDefault("director",""));
                            d.setRuntime(m.getOrDefault("runtime",""));
                            d.setStudio(m.getOrDefault("studio",""));
                            d.setLanguage(m.getOrDefault("language",""));
                            d.setSubtitles(m.getOrDefault("subtitles",""));
                            d.setReleaseDate(m.getOrDefault("releaseDate",""));
                            d.setGenre(m.getOrDefault("genre",""));
                            d.setBarcode(barcode);
                            d.setImagePath(imagePath);
                            p = d; break;
                        }
                        default: {
                            Product prod = new Product(id, title, original, current, weight, dimension, desc, barcode, imagePath);
                            p = prod; break;
                        }
                    }
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private static Map<String,String> parseExtra(String extra) {
        Map<String,String> m = new HashMap<>();
        if (extra == null) return m;
        String[] parts = extra.split(";;");
        for (String p: parts) {
            int idx = p.indexOf('=');
            if (idx>0) {
                String k = p.substring(0, idx);
                String v = p.substring(idx+1);
                m.put(k, v);
            }
        }
        return m;
    }
    
    /**
     * Add a new product to database (works with Media table)
     */
    public static long addProduct(Product product) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Get manager user_id
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Parse dimension
                Double width = null, height = null, length = null;
                if (product.getDimension() != null && !product.getDimension().isEmpty()) {
                    String[] parts = product.getDimension().replaceAll("[^0-9xX.]", "").split("[xX]");
                    if (parts.length >= 2) {
                        try {
                            width = Double.parseDouble(parts[0]);
                            height = Double.parseDouble(parts[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                
                // Insert into Media
                String sql = "INSERT INTO Media (category, barcode, title, description, price, value, quantity, weight, width, height, length, condition, image_url, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                long mediaId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    // Normalize category to lowercase for consistency
                    ps.setString(1, product.getType() != null ? product.getType().toLowerCase() : null);
                    ps.setString(2, product.getBarcode() != null ? product.getBarcode() : "PROD" + System.currentTimeMillis());
                    ps.setString(3, product.getTitle());
                    ps.setString(4, product.getDescription());
                    ps.setDouble(5, product.getCurrentPrice());
                    ps.setDouble(6, product.getOriginalValue());
                    ps.setInt(7, 10); // Default quantity
                    ps.setDouble(8, product.getWeight());
                    if (width != null) ps.setDouble(9, width);
                    else ps.setNull(9, Types.REAL);
                    if (height != null) ps.setDouble(10, height);
                    else ps.setNull(10, Types.REAL);
                    if (length != null) ps.setDouble(11, length);
                    else ps.setNull(11, Types.REAL);
                    ps.setString(12, "new");
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : ImageUtils.getProductImagePathAlways(0);
                    ps.setString(13, imagePath);
                    ps.setInt(14, managerUserId);
                    ps.setInt(15, managerUserId);
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            mediaId = rs.getLong(1);
                            // Update imagePath with actual ID
                            if (product.getImagePath() == null || product.getImagePath().isEmpty()) {
                                String newImagePath = ImageUtils.getProductImagePathAlways(mediaId);
                                updateMediaImagePath(conn, mediaId, newImagePath);
                            }
                            
                            // Insert into type-specific table
                            insertProductTypeDetails(conn, mediaId, product);
                            
                            conn.commit();
                            return mediaId;
                        }
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Update an existing product
     */
    public static boolean updateProduct(Product product) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Get manager user_id
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Parse dimension
                Double width = null, height = null, length = null;
                if (product.getDimension() != null && !product.getDimension().isEmpty()) {
                    String[] parts = product.getDimension().replaceAll("[^0-9xX.]", "").split("[xX]");
                    if (parts.length >= 2) {
                        try {
                            width = Double.parseDouble(parts[0]);
                            height = Double.parseDouble(parts[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                
                // Update Media
                String sql = "UPDATE Media SET category=?, barcode=?, title=?, description=?, price=?, value=?, weight=?, width=?, height=?, length=?, image_url=?, updated_by=?, updated_at=CURRENT_TIMESTAMP WHERE media_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    // Normalize category to lowercase for consistency
                    ps.setString(1, product.getType() != null ? product.getType().toLowerCase() : null);
                    ps.setString(2, product.getBarcode() != null ? product.getBarcode() : String.valueOf(product.getId()));
                    ps.setString(3, product.getTitle());
                    ps.setString(4, product.getDescription());
                    ps.setDouble(5, product.getCurrentPrice());
                    ps.setDouble(6, product.getOriginalValue());
                    ps.setDouble(7, product.getWeight());
                    if (width != null) ps.setDouble(8, width);
                    else ps.setNull(8, Types.REAL);
                    if (height != null) ps.setDouble(9, height);
                    else ps.setNull(9, Types.REAL);
                    if (length != null) ps.setDouble(10, length);
                    else ps.setNull(10, Types.REAL);
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : ImageUtils.getProductImagePathAlways(product.getId());
                    ps.setString(11, imagePath);
                    ps.setInt(12, managerUserId);
                    ps.setLong(13, product.getId());
                    
                    int affected = ps.executeUpdate();
                    if (affected > 0) {
                        // Update type-specific table
                        updateProductTypeDetails(conn, product.getId(), product);
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete a product by ID
     */
    public static boolean deleteProduct(long productId) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Delete from type-specific tables first
                String[] typeTables = {"Book", "Newspaper", "CD", "DVD"};
                for (String table : typeTables) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE media_id = ?")) {
                        ps.setLong(1, productId);
                        ps.executeUpdate();
                    }
                }
                
                // Delete from Media
                String sql = "DELETE FROM Media WHERE media_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, productId);
                    int affected = ps.executeUpdate();
                    if (affected > 0) {
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get a single product by ID
     */
    public static Product getProductById(long productId) {
        // Try Media first
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE m.media_id = ? AND m.status = 'active'";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMediaToProduct(conn, rs);
                }
            }
        } catch (SQLException e) {}
        
        // Fallback to products
        List<Product> products = queryProducts("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra,barcode,imagePath FROM products WHERE id=?", 
                            stmt -> {
                                stmt.setLong(1, productId);
                            });
        return products.isEmpty() ? null : products.get(0);
    }
    
    /**
     * Insert product type-specific details
     */
    private static void insertProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
        if (product instanceof Book) {
            Book b = (Book) product;
            insertBook(conn, mediaId, Map.of(
                "author", b.getAuthor() != null ? b.getAuthor() : "",
                "coverType", b.getCoverType() != null ? b.getCoverType() : "",
                "publisher", b.getPublisher() != null ? b.getPublisher() : "",
                "publicationDate", b.getPublicationDate() != null ? b.getPublicationDate() : "",
                "numberOfPages", b.getNumberOfPages() != null ? String.valueOf(b.getNumberOfPages()) : "",
                "language", b.getLanguage() != null ? b.getLanguage() : "",
                "genre", b.getGenre() != null ? b.getGenre() : ""
            ));
        } else if (product instanceof Newspaper) {
            Newspaper n = (Newspaper) product;
            insertNewspaper(conn, mediaId, Map.of(
                "editorInChief", n.getEditorInChief() != null ? n.getEditorInChief() : "",
                "publisher", n.getPublisher() != null ? n.getPublisher() : "",
                "publicationDate", n.getPublicationDate() != null ? n.getPublicationDate() : "",
                "issueNumber", n.getIssueNumber() != null ? n.getIssueNumber() : "",
                "publicationFrequency", n.getPublicationFrequency() != null ? n.getPublicationFrequency() : "",
                "issn", n.getIssn() != null ? n.getIssn() : "",
                "language", n.getLanguage() != null ? n.getLanguage() : "",
                "sections", n.getSections() != null ? n.getSections() : ""
            ));
        } else if (product instanceof CD) {
            CD c = (CD) product;
            insertCD(conn, mediaId, Map.of(
                "artist", c.getArtist() != null ? c.getArtist() : "",
                "recordLabel", c.getRecordLabel() != null ? c.getRecordLabel() : "",
                "album", c.getAlbum() != null ? c.getAlbum() : "",
                "releaseDate", c.getReleaseDate() != null ? c.getReleaseDate() : "",
                "genre", c.getGenre() != null ? c.getGenre() : ""
            ));
        } else if (product instanceof DVD) {
            DVD d = (DVD) product;
            insertDVD(conn, mediaId, Map.of(
                "discType", d.getDiscType() != null ? d.getDiscType() : "",
                "director", d.getDirector() != null ? d.getDirector() : "",
                "runtime", d.getRuntime() != null ? String.valueOf(d.getRuntime()) : "",
                "studio", d.getStudio() != null ? d.getStudio() : "",
                "language", d.getLanguage() != null ? d.getLanguage() : "",
                "subtitles", d.getSubtitles() != null ? d.getSubtitles() : "",
                "releaseDate", d.getReleaseDate() != null ? d.getReleaseDate() : "",
                "genre", d.getGenre() != null ? d.getGenre() : ""
            ));
        }
    }
    
    /**
     * Update product type-specific details
     */
    private static void updateProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
        // Delete old type-specific records
        String[] typeTables = {"Book", "Newspaper", "CD", "DVD"};
        for (String table : typeTables) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE media_id = ?")) {
                ps.setLong(1, mediaId);
                ps.executeUpdate();
            }
        }
        
        // Insert new type-specific details
        insertProductTypeDetails(conn, mediaId, product);
    }
    
    /**
     * Update Media image path
     */
    private static void updateMediaImagePath(Connection conn, long mediaId, String imagePath) throws SQLException {
        String sql = "UPDATE Media SET image_url=? WHERE media_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, mediaId);
            ps.executeUpdate();
        }
    }
}
