package com.aims.dao;

import com.aims.entity.media.Book;
import com.aims.entity.media.Media;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DaoTest {

    private final BookDao bookDao = new BookDao();
    private final MediaDao mediaDao = new MediaDao();

    /**
     * Ensure {@link BookDao#getAll()} returns a non-null list and does not throw when the
     * database connection is correctly configured (same assumption as {@code DBconnectionTest}).
     */
    @Test
    void bookDaoGetAllShouldReturnNonNullList() throws SQLException {
        List<Book> books = bookDao.getAll();

        assertNotNull(books, "BookDao#getAll should never return null");
    }

    /**
     * Requesting a book by an obviously invalid id should simply return an empty {@link Optional}.
     */
    @Test
    void bookDaoGetWithInvalidIdShouldReturnEmptyOptional() throws SQLException {
        Optional<Book> result = bookDao.get(-1); // assuming there is no book with id -1

        assertTrue(result.isEmpty(), "BookDao#get with a non-existing id should return Optional.empty()");
    }

    /**
     * Ensure {@link MediaDao#getAll()} aggregates media without throwing and returns a non-null list.
     */
    @Test
    void mediaDaoGetAllShouldReturnNonNullList() throws SQLException {
        List<Media> mediaList = mediaDao.getAll();

        assertNotNull(mediaList, "MediaDao#getAll should never return null");
    }

    /**
     * Requesting media by an obviously invalid id should simply return an empty {@link Optional}.
     */
    @Test
    void mediaDaoGetWithInvalidIdShouldReturnEmptyOptional() throws SQLException {
        Optional<Media> result = mediaDao.get(-1); // assuming there is no media with id -1

        assertTrue(result.isEmpty(), "MediaDao#get with a non-existing id should return Optional.empty()");
    }
}
