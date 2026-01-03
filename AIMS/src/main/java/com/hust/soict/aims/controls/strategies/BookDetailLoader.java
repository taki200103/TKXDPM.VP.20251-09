package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Book;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.dao.BookDAO;
import com.hust.soict.aims.dao.impl.BookDAOImpl;
import javax.swing.JPanel;
import java.sql.Connection;

/**
 * Strategy Pattern - Concrete Strategy for Book products
 * 
 * This class implements the ProductDetailLoader interface specifically for Book products.
 * It knows how to:
 * 1. Load additional Book-specific data from the Book table (JOIN with Media)
 * 2. Display all Book-related information in the UI
 */
public class BookDetailLoader implements ProductDetailLoader {
    
    private BookDAO bookDAO;
    
    public BookDetailLoader() {
        this.bookDAO = new BookDAOImpl();
    }
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof Book)) {
            return product;
        }
        
        Book book = (Book) product;
        long mediaId = book.getId();
        
        // Strategy Pattern: Load Book-specific details using DAO
        bookDAO.loadBookDetails(conn, book, mediaId);
        
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
