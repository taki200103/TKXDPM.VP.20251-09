package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.BookDAO;
import com.hust.soict.aims.entities.Book;

import java.sql.*;

/**
 * DAO Implementation for Book-specific operations
 * Contains SQL queries and database access logic
 */
public class BookDAOImpl extends BaseDAO implements BookDAO {
    
    @Override
    public boolean loadBookDetails(Connection conn, Book book, long mediaId) {
        String sql = "SELECT author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre " +
                     "FROM Book WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book.setAuthor(rs.getString("author"));
                    book.setCoverType(rs.getString("cover_type"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPublicationDate(rs.getString("publish_date"));
                    book.setNumberOfPages(rs.getObject("number_of_page") != null ? rs.getInt("number_of_page") : null);
                    book.setLanguage(rs.getString("language"));
                    book.setBookCategory(rs.getString("book_category"));
                    book.setGenre(rs.getString("genre"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading Book details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean loadBookDetails(Book book, long mediaId) {
        try (Connection conn = getConnection()) {
            return loadBookDetails(conn, book, mediaId);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean saveBookDetails(Connection conn, long mediaId, Book book) throws SQLException {
        // Check if Book record exists
        String checkSql = "SELECT COUNT(*) FROM Book WHERE media_id = ?";
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
            String updateSql = "UPDATE Book SET author=?, cover_type=?, publisher=?, publish_date=?, " +
                              "number_of_page=?, language=?, book_category=?, genre=? WHERE media_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, book.getAuthor());
                ps.setString(2, book.getCoverType());
                ps.setString(3, book.getPublisher());
                ps.setString(4, book.getPublicationDate());
                if (book.getNumberOfPages() != null) {
                    ps.setInt(5, book.getNumberOfPages());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setString(6, book.getLanguage());
                ps.setString(7, book.getBookCategory());
                ps.setString(8, book.getGenre());
                ps.setLong(9, mediaId);
                ps.executeUpdate();
            }
        } else {
            // Insert new record
            String insertSql = "INSERT INTO Book (media_id, author, cover_type, publisher, publish_date, " +
                              "number_of_page, language, book_category, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, mediaId);
                ps.setString(2, book.getAuthor());
                ps.setString(3, book.getCoverType());
                ps.setString(4, book.getPublisher());
                ps.setString(5, book.getPublicationDate());
                if (book.getNumberOfPages() != null) {
                    ps.setInt(6, book.getNumberOfPages());
                } else {
                    ps.setNull(6, Types.INTEGER);
                }
                ps.setString(7, book.getLanguage());
                ps.setString(8, book.getBookCategory());
                ps.setString(9, book.getGenre());
                ps.executeUpdate();
            }
        }
        return true;
    }
    
    @Override
    public boolean deleteBookDetails(Connection conn, long mediaId) throws SQLException {
        String sql = "DELETE FROM Book WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.executeUpdate();
        }
        return true;
    }
}

