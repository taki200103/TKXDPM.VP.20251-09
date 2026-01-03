package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.DVDDAO;
import com.hust.soict.aims.entities.DVD;

import java.sql.*;

/**
 * DAO Implementation for DVD-specific operations
 * Contains SQL queries and database access logic
 */
public class DVDDAOImpl extends BaseDAO implements DVDDAO {
    
    @Override
    public boolean loadDVDDetails(Connection conn, DVD dvd, long mediaId) {
        String sql = "SELECT disc_type, director, runtime, studio, language, subtitle, release_date, genre " +
                     "FROM DVD WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dvd.setDiscType(rs.getString("disc_type"));
                    dvd.setDirector(rs.getString("director"));
                    dvd.setRuntime(rs.getObject("runtime") != null ? rs.getInt("runtime") : null);
                    dvd.setStudio(rs.getString("studio"));
                    dvd.setLanguage(rs.getString("language"));
                    dvd.setSubtitles(rs.getString("subtitle"));
                    dvd.setReleaseDate(rs.getString("release_date"));
                    dvd.setGenre(rs.getString("genre"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading DVD details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean loadDVDDetails(DVD dvd, long mediaId) {
        try (Connection conn = getConnection()) {
            return loadDVDDetails(conn, dvd, mediaId);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean saveDVDDetails(Connection conn, long mediaId, DVD dvd) throws SQLException {
        // Check if DVD record exists
        String checkSql = "SELECT COUNT(*) FROM DVD WHERE media_id = ?";
        boolean exists = false;
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        }
        
        if (exists) {
            // Update existing record
            String updateSql = "UPDATE DVD SET disc_type=?, director=?, runtime=?, studio=?, " +
                              "language=?, subtitle=?, release_date=?, genre=? WHERE media_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, dvd.getDiscType());
                ps.setString(2, dvd.getDirector());
                if (dvd.getRuntime() != null) {
                    ps.setInt(3, dvd.getRuntime());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, dvd.getStudio());
                ps.setString(5, dvd.getLanguage());
                ps.setString(6, dvd.getSubtitles());
                ps.setString(7, dvd.getReleaseDate());
                ps.setString(8, dvd.getGenre());
                ps.setLong(9, mediaId);
                ps.executeUpdate();
            }
        } else {
            // Insert new record
            String insertSql = "INSERT INTO DVD (media_id, disc_type, director, runtime, studio, " +
                              "language, subtitle, release_date, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, mediaId);
                ps.setString(2, dvd.getDiscType());
                ps.setString(3, dvd.getDirector());
                if (dvd.getRuntime() != null) {
                    ps.setInt(4, dvd.getRuntime());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setString(5, dvd.getStudio());
                ps.setString(6, dvd.getLanguage());
                ps.setString(7, dvd.getSubtitles());
                ps.setString(8, dvd.getReleaseDate());
                ps.setString(9, dvd.getGenre());
                ps.executeUpdate();
            }
        }
        return true;
    }
    
    @Override
    public boolean deleteDVDDetails(Connection conn, long mediaId) throws SQLException {
        String sql = "DELETE FROM DVD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.executeUpdate();
        }
        return true;
    }
}

