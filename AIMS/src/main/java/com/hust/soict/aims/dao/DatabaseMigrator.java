package com.hust.soict.aims.dao;

import com.hust.soict.aims.utils.ImageUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Migrator
 * Chèn dữ liệu vào database 
 */
public class DatabaseMigrator {

    private static final String URL = BaseDAO.URL;
    
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

