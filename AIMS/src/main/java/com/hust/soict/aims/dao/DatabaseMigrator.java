package com.hust.soict.aims.dao;

import com.hust.soict.aims.utils.ImageUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Migrator
 * Responsible for migrating data from legacy products table to new Media schema
 */
public class DatabaseMigrator {
    private static final String URL = BaseDAO.URL;
    
    /**
     * Migrate data from legacy products table to new Media schema
     */
    public static void migrate() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            migrateProductsToMedia(conn);
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
        String sql = "INSERT INTO Book (media_id, author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(8, extra.getOrDefault("bookCategory", null));
            ps.setString(9, extra.getOrDefault("genre", null));
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
    
    /**
     * Parse extra field from legacy products table
     */
    private static Map<String, String> parseExtra(String extra) {
        Map<String, String> m = new HashMap<>();
        if (extra == null) return m;
        String[] parts = extra.split(";;");
        for (String p: parts) {
            int idx = p.indexOf('=');
            if (idx > 0) {
                String k = p.substring(0, idx);
                String v = p.substring(idx + 1);
                m.put(k, v);
            }
        }
        return m;
    }
}

