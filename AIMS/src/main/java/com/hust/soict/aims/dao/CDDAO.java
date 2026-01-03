package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.CD;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO Interface for CD-specific operations
 * Defines contract for CD data access operations
 */
public interface CDDAO {
    
    /**
     * Load CD-specific details from database
     * @param conn Database connection
     * @param cd CD object to populate
     * @param mediaId Media ID
     * @return true if CD details were found and loaded
     */
    boolean loadCDDetails(Connection conn, CD cd, long mediaId);
    
    /**
     * Load CD details using a new connection
     * @param cd CD object to populate
     * @param mediaId Media ID
     * @return true if CD details were found and loaded
     */
    boolean loadCDDetails(CD cd, long mediaId);
    
    /**
     * Insert or update CD-specific details
     * @param conn Database connection (for transaction support)
     * @param mediaId Media ID
     * @param cd CD object with details
     * @return true if successful
     */
    boolean saveCDDetails(Connection conn, long mediaId, CD cd) throws SQLException;
    
    /**
     * Delete CD-specific details
     * @param conn Database connection
     * @param mediaId Media ID
     * @return true if successful
     */
    boolean deleteCDDetails(Connection conn, long mediaId) throws SQLException;
}
