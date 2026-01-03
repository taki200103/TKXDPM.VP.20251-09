package com.hust.soict.aims.utils;

import com.hust.soict.aims.entities.*;

import java.sql.*;

/**
 * Product Mapper Utility
 * Maps database ResultSet to Product objects
 */
public class ProductMapper {
    
    /**
     * Map Media row to Product object
     * This method is used by DAO classes to convert ResultSet to Product entities
     */
    public static Product mapMediaToProduct(Connection conn, ResultSet rs) throws SQLException {
        long mediaId = rs.getLong("media_id");
        String category = rs.getString("category");
        String barcode = rs.getString("barcode");
        String title = rs.getString("title");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        double value = rs.getDouble("value");
        int quantity = rs.getInt("quantity");
        double weight = rs.getDouble("weight");
        Double width = rs.getObject("width") != null ? rs.getDouble("width") : null;
        Double height = rs.getObject("height") != null ? rs.getDouble("height") : null;
        Double length = rs.getObject("length") != null ? rs.getDouble("length") : null;
        String condition = rs.getString("condition");
        String status = rs.getString("status");
        String imageUrl = rs.getString("image_url");
        Integer createdBy = rs.getObject("created_by") != null ? rs.getInt("created_by") : null;
        Integer updatedBy = rs.getObject("updated_by") != null ? rs.getInt("updated_by") : null;
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        
        // Build dimension string
        String dimension = "";
        if (width != null && height != null) {
            dimension = width.intValue() + "x" + height.intValue() + "cm";
        }
        
        Product p = null;
        switch (category.toLowerCase()) {
            case "book": {
                Book b = loadBookDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = b;
                break;
            }
            case "newspaper": {
                Newspaper n = loadNewspaperDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = n;
                break;
            }
            case "cd": {
                CD c = loadCDDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = c;
                break;
            }
            case "dvd": {
                DVD d = loadDVDDetails(conn, mediaId, mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                p = d;
                break;
            }
            default: {
                p = new Product(mediaId, title, value, price, weight, dimension, description, barcode, imageUrl);
                break;
            }
        }
        
        // Set common fields
        p.setCategory(category);
        p.setQuantity(quantity);
        p.setWidth(width);
        p.setHeight(height);
        p.setLength(length);
        if (condition != null) p.setCondition(condition);
        if (status != null) p.setStatus(status);
        p.setCreatedBy(createdBy);
        p.setUpdatedBy(updatedBy);
        p.setCreatedAt(createdAt);
        p.setUpdatedAt(updatedAt);
        
        return p;
    }
    
    private static Book loadBookDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre FROM Book WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book(id, title, value, price, weight, dimension, description,
                        rs.getString("author"), rs.getString("cover_type"), rs.getString("publisher"), 
                        rs.getString("publish_date"));
                    b.setNumberOfPages(rs.getObject("number_of_page") != null ? rs.getInt("number_of_page") : null);
                    b.setLanguage(rs.getString("language"));
                    b.setBookCategory(rs.getString("book_category"));
                    b.setGenre(rs.getString("genre"));
                    b.setBarcode(barcode);
                    b.setImagePath(imageUrl);
                    return b;
                }
            }
        }
        return new Book(id, title, value, price, weight, dimension, description, "", "", "", "");
    }
    
    private static Newspaper loadNewspaperDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT editor_in_chief, publisher, publish_date, issue_number, publication_frequency, issn, language, sections FROM Newspaper WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Newspaper n = new Newspaper(id, title, value, price, weight, dimension, description,
                        rs.getString("editor_in_chief"), rs.getString("publisher"), rs.getString("publish_date"));
                    n.setIssueNumber(rs.getString("issue_number"));
                    n.setPublicationFrequency(rs.getString("publication_frequency"));
                    n.setIssn(rs.getString("issn"));
                    n.setLanguage(rs.getString("language"));
                    n.setSections(rs.getString("sections"));
                    n.setBarcode(barcode);
                    n.setImagePath(imageUrl);
                    return n;
                }
            }
        }
        return new Newspaper(id, title, value, price, weight, dimension, description, "", "", "");
    }
    
    private static CD loadCDDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT artist, record_label, music_type, release_date, genre FROM CD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CD c = new CD(id, title, value, price, weight, dimension, description,
                        rs.getString("music_type"), rs.getString("artist"), rs.getString("record_label"));
                    c.setGenre(rs.getString("genre"));
                    c.setReleaseDate(rs.getString("release_date"));
                    c.setBarcode(barcode);
                    c.setImagePath(imageUrl);
                    return c;
                }
            }
        }
        return new CD(id, title, value, price, weight, dimension, description, "", "", "");
    }
    
    private static DVD loadDVDDetails(Connection conn, long mediaId, long id, String title, double value, double price, double weight, String dimension, String description, String barcode, String imageUrl) throws SQLException {
        String sql = "SELECT disc_type, director, runtime, studio, language, subtitle, release_date, genre FROM DVD WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DVD d = new DVD(id, title, value, price, weight, dimension, description,
                        rs.getString("disc_type"), rs.getString("director"));
                    d.setRuntime(rs.getObject("runtime") != null ? rs.getInt("runtime") : null);
                    d.setStudio(rs.getString("studio"));
                    d.setLanguage(rs.getString("language"));
                    d.setSubtitles(rs.getString("subtitle"));
                    d.setReleaseDate(rs.getString("release_date"));
                    d.setGenre(rs.getString("genre"));
                    d.setBarcode(barcode);
                    d.setImagePath(imageUrl);
                    return d;
                }
            }
        }
        return new DVD(id, title, value, price, weight, dimension, description, "", "");
    }
}

