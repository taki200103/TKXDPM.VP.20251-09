package com.aims.entity.db;
import java.sql.Connection;

public interface IDatabaseConnector {
    Connection connect();
}
