package com.aims.entity.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DBconnectionTest {

    @Test
    public void testPostgresConnection() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DBconnection.getConnection()) {
                assertNotNull(connection, "Connection should not be null");
                assertFalse(connection.isClosed(), "Connection should be open");
            }   
        });
    }
}
