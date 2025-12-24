package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Product;
import javax.swing.JPanel;
import java.sql.Connection;

/**
 * Strategy Pattern Interface
 * 
 * This interface defines the contract for loading and displaying product-specific details.
 * Each product type (Book, CD, DVD, Newspaper) will have its own implementation
 * that knows how to load additional data from the appropriate database tables
 * and how to display that information in the UI.
 * 
 * Strategy Pattern is used here to:
 * - Encapsulate the algorithm for loading product-specific details
 * - Allow runtime selection of the appropriate loading strategy
 * - Make it easy to add new product types without modifying existing code
 */
public interface ProductDetailLoader {
    
    /**
     * Load additional product-specific details from database
     * This method should join with the appropriate type-specific table
     * (e.g., Book, CD, DVD, Newspaper) to fetch all related information
     * 
     * @param conn Database connection
     * @param product The base product (may already have some data loaded)
     * @return The product with all type-specific details loaded
     */
    Product loadDetails(Connection conn, Product product);
    
    /**
     * Display product-specific details in the UI panel
     * This method adds type-specific information fields to the detail panel
     * 
     * @param detailPanel The panel to add detail rows to
     * @param product The product with loaded details
     */
    void displayDetails(JPanel detailPanel, Product product);
}
