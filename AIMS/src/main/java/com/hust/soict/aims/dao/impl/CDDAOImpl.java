package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.CDDAO;
import com.hust.soict.aims.entities.CD;

import java.sql.*;

/**
 * DAO Implementation for CD-specific operations
 * Contains SQL queries and database access logic
 */
public class CDDAOImpl extends BaseDAO implements CDDAO {
    
    @Override
    public boolean loadCDDetails(Connection conn, CD cd, long mediaId) {
        String sql = "SELECT artist, record_label, music_type, release_date, genre " +
                     "FROM CD WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cd.setArtist(rs.getString("artist"));
                    cd.setRecordLabel(rs.getString("record_label"));
                    cd.setAlbum(rs.getString("music_type")); // album maps to music_type in DB
                    cd.setReleaseDate(rs.getString("release_date"));
                    cd.setGenre(rs.getString("genre"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading CD details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean loadCDDetails(CD cd, long mediaId) {
        try (Connection conn = getConnection()) {
            return loadCDDetails(conn, cd, mediaId);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean saveCDDetails(Connection conn, long mediaId, CD cd) throws SQLException {
        // Check if CD record exists
        String checkSql = "SELECT COUNT(*) FROM CD WHERE media_id = ?";
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
            String updateSql = "UPDATE CD SET artist=?, record_label=?, music_type=?, release_date=?, genre=? WHERE media_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, cd.getArtist());
                ps.setString(2, cd.getRecordLabel());
                ps.setString(3, cd.getAlbum()); // album maps to music_type
                ps.setString(4, cd.getReleaseDate());
                ps.setString(5, cd.getGenre());
                ps.setLong(6, mediaId);
                ps.executeUpdate();
            }
        } else {
            // Insert new record
            String insertSql = "INSERT INTO CD (media_id, artist, record_label, music_type, release_date, genre) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, mediaId);
                ps.setString(2, cd.getArtist());
                ps.setString(3, cd.getRecordLabel());
                ps.setString(4, cd.getAlbum()); // album maps to music_type
                ps.setString(5, cd.getReleaseDate());
                ps.setString(6, cd.getGenre());
                ps.executeUpdate();
            }
        }
        return true;
    }
    
    @Override
    public boolean deleteCDDetails(Connection conn, long mediaId) throws SQLException {
        String sql = "DELETE FROM CD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.executeUpdate();
        }
        return true;
    }
}

