package com.hust.soict.aims.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Base DAO class providing common database connection management
 * All DAO classes should extend this base class
 */
public abstract class BaseDAO {
    private static final String DB_FILE = "aims.db";
    protected static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    /**
     * Get a database connection with auto-commit disabled
     * Useful for transactions
     * @return Connection object with auto-commit disabled
     * @throws SQLException if connection fails
     */
    protected Connection getConnectionWithTransaction() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        conn.setAutoCommit(false);
        return conn;
    }
}

