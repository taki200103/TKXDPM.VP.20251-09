package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.NewspaperDAO;
import com.hust.soict.aims.entities.Newspaper;

import java.sql.*;

/**
 * DAO Implementation for Newspaper-specific operations
 * Contains SQL queries and database access logic
 */
public class NewspaperDAOImpl extends BaseDAO implements NewspaperDAO {
    
    @Override
    public boolean loadNewspaperDetails(Connection conn, Newspaper newspaper, long mediaId) {
        String sql = "SELECT editor_in_chief, publisher, publish_date, issue_number, " +
                     "publication_frequency, issn, language, sections " +
                     "FROM Newspaper WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    newspaper.setEditorInChief(rs.getString("editor_in_chief"));
                    newspaper.setPublisher(rs.getString("publisher"));
                    newspaper.setPublicationDate(rs.getString("publish_date"));
                    newspaper.setIssueNumber(rs.getString("issue_number"));
                    newspaper.setPublicationFrequency(rs.getString("publication_frequency"));
                    newspaper.setIssn(rs.getString("issn"));
                    newspaper.setLanguage(rs.getString("language"));
                    newspaper.setSections(rs.getString("sections"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading Newspaper details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean loadNewspaperDetails(Newspaper newspaper, long mediaId) {
        try (Connection conn = getConnection()) {
            return loadNewspaperDetails(conn, newspaper, mediaId);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean saveNewspaperDetails(Connection conn, long mediaId, Newspaper newspaper) throws SQLException {
        // Check if Newspaper record exists
        String checkSql = "SELECT COUNT(*) FROM Newspaper WHERE media_id = ?";
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
            String updateSql = "UPDATE Newspaper SET editor_in_chief=?, publisher=?, publish_date=?, " +
                              "issue_number=?, publication_frequency=?, issn=?, language=?, sections=? WHERE media_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newspaper.getEditorInChief());
                ps.setString(2, newspaper.getPublisher());
                ps.setString(3, newspaper.getPublicationDate());
                ps.setString(4, newspaper.getIssueNumber());
                ps.setString(5, newspaper.getPublicationFrequency());
                ps.setString(6, newspaper.getIssn());
                ps.setString(7, newspaper.getLanguage());
                ps.setString(8, newspaper.getSections());
                ps.setLong(9, mediaId);
                ps.executeUpdate();
            }
        } else {
            // Insert new record
            String insertSql = "INSERT INTO Newspaper (media_id, editor_in_chief, publisher, publish_date, " +
                              "issue_number, publication_frequency, issn, language, sections) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, mediaId);
                ps.setString(2, newspaper.getEditorInChief());
                ps.setString(3, newspaper.getPublisher());
                ps.setString(4, newspaper.getPublicationDate());
                ps.setString(5, newspaper.getIssueNumber());
                ps.setString(6, newspaper.getPublicationFrequency());
                ps.setString(7, newspaper.getIssn());
                ps.setString(8, newspaper.getLanguage());
                ps.setString(9, newspaper.getSections());
                ps.executeUpdate();
            }
        }
        return true;
    }
    
    @Override
    public boolean deleteNewspaperDetails(Connection conn, long mediaId) throws SQLException {
        String sql = "DELETE FROM Newspaper WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.executeUpdate();
        }
        return true;
    }
}

