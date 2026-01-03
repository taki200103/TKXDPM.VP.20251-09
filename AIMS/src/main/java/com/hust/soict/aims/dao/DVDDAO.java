package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.DVD;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO Interface for DVD-specific operations
 * Defines contract for DVD data access operations
 */
public interface DVDDAO {
    
    /**
     * Load DVD-specific details from database
     * @param conn Database connection
     * @param dvd DVD object to populate
     * @param mediaId Media ID
     * @return true if DVD details were found and loaded
     */
    boolean loadDVDDetails(Connection conn, DVD dvd, long mediaId);
    
    /**
     * Load DVD details using a new connection
     * @param dvd DVD object to populate
     * @param mediaId Media ID
     * @return true if DVD details were found and loaded
     */
    boolean loadDVDDetails(DVD dvd, long mediaId);
    
    /**
     * Insert or update DVD-specific details
     * @param conn Database connection (for transaction support)
     * @param mediaId Media ID
     * @param dvd DVD object with details
     * @return true if successful
     */
    boolean saveDVDDetails(Connection conn, long mediaId, DVD dvd) throws SQLException;
    
    /**
     * Delete DVD-specific details
     * @param conn Database connection
     * @param mediaId Media ID
     * @return true if successful
     */
    boolean deleteDVDDetails(Connection conn, long mediaId) throws SQLException;
}
