package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Book;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import javax.swing.JPanel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Strategy Pattern - Concrete Strategy for Book products
 * 
 * This class implements the ProductDetailLoader interface specifically for Book products.
 * It knows how to:
 * 1. Load additional Book-specific data from the Book table (JOIN with Media)
 * 2. Display all Book-related information in the UI
 */
public class BookDetailLoader implements ProductDetailLoader {
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof Book)) {
            return product;
        }
        
        Book book = (Book) product;
        long mediaId = book.getId();
        
        // Strategy Pattern: Load Book-specific details by joining Book table with Media
        String sql = "SELECT author, cover_type, publisher, publish_date, number_of_page, " +
                     "language, book_category, genre " +
                     "FROM Book WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book.setAuthor(rs.getString("author"));
                    book.setCoverType(rs.getString("cover_type"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPublicationDate(rs.getString("publish_date"));
                    
                    Integer numberOfPages = rs.getObject("number_of_page") != null ? 
                                          rs.getInt("number_of_page") : null;
                    book.setNumberOfPages(numberOfPages);
                    
                    book.setLanguage(rs.getString("language"));
                    book.setBookCategory(rs.getString("book_category"));
                    book.setGenre(rs.getString("genre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading Book details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return book;
    }
    
    @Override
    public void displayDetails(JPanel detailPanel, Product product) {
        if (!(product instanceof Book)) {
            return;
        }
        
        Book book = (Book) product;
        
        // Strategy Pattern: Display all Book-specific information
        ProductDetailScreen.addDetailRow(detailPanel, "Author:", book.getAuthor());
        ProductDetailScreen.addDetailRow(detailPanel, "Cover Type:", book.getCoverType());
        ProductDetailScreen.addDetailRow(detailPanel, "Publisher:", book.getPublisher());
        ProductDetailScreen.addDetailRow(detailPanel, "Publication Date:", book.getPublicationDate());
        
        if (book.getNumberOfPages() != null) {
            ProductDetailScreen.addDetailRow(detailPanel, "Number of Pages:", 
                                           String.valueOf(book.getNumberOfPages()));
        }
        
        ProductDetailScreen.addDetailRow(detailPanel, "Language:", book.getLanguage());
        ProductDetailScreen.addDetailRow(detailPanel, "Book Category:", book.getBookCategory());
        ProductDetailScreen.addDetailRow(detailPanel, "Genre:", book.getGenre());
    }
}
