package com.aims.entity.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnector implements IDatabaseConnector {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/AIMS";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "200103";

    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(
                    DEFAULT_URL,
                    DEFAULT_USER,
                    DEFAULT_PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to PostgreSQL", e);
        }
    }

}
