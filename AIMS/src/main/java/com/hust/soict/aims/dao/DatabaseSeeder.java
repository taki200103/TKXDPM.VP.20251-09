package com.hust.soict.aims.dao;

import com.hust.soict.aims.utils.ImageUtils;

import java.sql.*;
import java.util.Map;

/**
 * Database Seeder
 * Responsible for seeding sample data into Media table
 */
public class DatabaseSeeder {
    private static final String URL = BaseDAO.URL;
    
    /**
     * Seed Media table with sample data if it's empty
     */
    public static void seed() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
            if (rs.next() && rs.getInt(1) == 0) {
                seedMedia(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                "publicationDate", "2020-01-0" + ((i % 9) + 1),
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
                "publicationDate", "2025-10-0" + ((i % 9) + 1),
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
                "releaseDate", "2019-05-0" + ((i % 9) + 1),
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
                "releaseDate", "2018-0" + ((i % 9) + 1),
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
}

