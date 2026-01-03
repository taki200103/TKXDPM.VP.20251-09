package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Newspaper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO Interface for Newspaper-specific operations
 * Defines contract for Newspaper data access operations
 */
public interface NewspaperDAO {
    
    /**
     * Load Newspaper-specific details from database
     * @param conn Database connection
     * @param newspaper Newspaper object to populate
     * @param mediaId Media ID
     * @return true if Newspaper details were found and loaded
     */
    boolean loadNewspaperDetails(Connection conn, Newspaper newspaper, long mediaId);
    
    /**
     * Load Newspaper details using a new connection
     * @param newspaper Newspaper object to populate
     * @param mediaId Media ID
     * @return true if Newspaper details were found and loaded
     */
    boolean loadNewspaperDetails(Newspaper newspaper, long mediaId);
    
    /**
     * Insert or update Newspaper-specific details
     * @param conn Database connection (for transaction support)
     * @param mediaId Media ID
     * @param newspaper Newspaper object with details
     * @return true if successful
     */
    boolean saveNewspaperDetails(Connection conn, long mediaId, Newspaper newspaper) throws SQLException;
    
    /**
     * Delete Newspaper-specific details
     * @param conn Database connection
     * @param mediaId Media ID
     * @return true if successful
     */
    boolean deleteNewspaperDetails(Connection conn, long mediaId) throws SQLException;
}
