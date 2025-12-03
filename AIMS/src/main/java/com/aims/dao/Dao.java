package com.aims.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> getAll() throws SQLException;
    Optional<T> get(int id) throws SQLException;
    void save(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t);
}
