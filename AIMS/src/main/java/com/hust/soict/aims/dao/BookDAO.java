package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Book;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO Interface for Book-specific operations
 * Defines contract for Book data access operations
 */
public interface BookDAO {
    
    /**
     * Load Book-specific details from database
     * @param conn Database connection
     * @param book Book object to populate
     * @param mediaId Media ID
     * @return true if Book details were found and loaded
     */
    boolean loadBookDetails(Connection conn, Book book, long mediaId);
    
    /**
     * Load Book details using a new connection
     * @param book Book object to populate
     * @param mediaId Media ID
     * @return true if Book details were found and loaded
     */
    boolean loadBookDetails(Book book, long mediaId);
    
    /**
     * Insert or update Book-specific details
     * @param conn Database connection (for transaction support)
     * @param mediaId Media ID
     * @param book Book object with details
     * @return true if successful
     */
    boolean saveBookDetails(Connection conn, long mediaId, Book book) throws SQLException;
    
    /**
     * Delete Book-specific details
     * @param conn Database connection
     * @param mediaId Media ID
     * @return true if successful
     */
    boolean deleteBookDetails(Connection conn, long mediaId) throws SQLException;
}
