package com.aims.entity.media;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MediaTest {

    @Test
    void fullConstructorShouldPersistValues() throws SQLException {
        Media media = new Media(
                1,
                "Test Title",
                "Book",
                100,
                5,
                "http://example.com/image.png",
                "Test description",
                0.5
        );

        assertEquals(1, media.getId());
        assertEquals("Test Title", media.getTitle());
        assertEquals("Book", media.getCategory());
        assertEquals(100, media.getPrice());
        assertEquals(5, media.getQuantity());
        assertEquals("http://example.com/image.png", media.getImageURL());
        assertEquals("Test description", media.getDescription());
        assertEquals(0.5, media.getWeight());
    }

    @Test
    void settersShouldSupportChainingAndPersistValues() throws SQLException {
        Media media = new Media();

        assertDoesNotThrow(() -> media
                .setTitle("New Title")
                .setCategory("DVD")
                .setPrice(250)
                .setQuantity(10)
                .setMediaURL("http://example.com/cover.jpg")
                .setDescription("Testing setters")
                .setWeight(1.2)
        );

        assertEquals("New Title", media.getTitle());
        assertEquals("DVD", media.getCategory());
        assertEquals(250, media.getPrice());
        assertEquals(10, media.getQuantity());
        assertEquals("http://example.com/cover.jpg", media.getImageURL());
        assertEquals("Testing setters", media.getDescription());
        assertEquals(1.2, media.getWeight());
    }
}
