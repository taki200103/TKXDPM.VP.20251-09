package com.aims.entity.db;

import java.sql.Connection;

public final class DBconnection {
    private static Connection connection;
    private static IDatabaseConnector connector = new PostgresConnector();

    private DBconnection() {
    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            connection = connector.connect();
        }
        return connection;
    }
}
