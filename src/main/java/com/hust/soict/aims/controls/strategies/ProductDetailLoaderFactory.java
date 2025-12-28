package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.entities.Book;
import com.hust.soict.aims.entities.CD;
import com.hust.soict.aims.entities.DVD;
import com.hust.soict.aims.entities.Newspaper;

/**
 * Factory Pattern
 * 
 * This factory class is responsible for creating the appropriate ProductDetailLoader
 * based on the product type. It encapsulates the logic for determining which strategy
 * to use, making it easy to add new product types in the future.
 * 
 * Factory Pattern is used here to:
 * - Centralize the creation logic for ProductDetailLoader instances
 * - Hide the complexity of selecting the right loader
 * - Make it easy to extend with new product types without modifying client code
 */
public class ProductDetailLoaderFactory {
    
    /**
     * Factory Method Pattern: Get the appropriate ProductDetailLoader for a product
     * 
     * @param product The product to get a loader for
     * @return The appropriate ProductDetailLoader strategy, or null if product type is unknown
     */
    public static ProductDetailLoader getLoader(Product product) {
        if (product == null) {
            return null;
        }
        
        // Factory Pattern: Return the appropriate strategy based on product type
        if (product instanceof Book) {
            return new BookDetailLoader();
        } else if (product instanceof CD) {
            return new CDDetailLoader();
        } else if (product instanceof DVD) {
            return new DVDDetailLoader();
        } else if (product instanceof Newspaper) {
            return new NewspaperDetailLoader();
        }
        
        // Default: no specific loader for generic Product
        return null;
    }
    
    /**
     * Factory Method Pattern: Get the appropriate ProductDetailLoader by category string
     * 
     * @param category The product category (book, cd, dvd, newspaper)
     * @return The appropriate ProductDetailLoader strategy, or null if category is unknown
     */
    public static ProductDetailLoader getLoaderByCategory(String category) {
        if (category == null) {
            return null;
        }
        
        String cat = category.toLowerCase();
        
        // Factory Pattern: Return the appropriate strategy based on category
        switch (cat) {
            case "book":
                return new BookDetailLoader();
            case "cd":
                return new CDDetailLoader();
            case "dvd":
                return new DVDDetailLoader();
            case "newspaper":
                return new NewspaperDetailLoader();
            default:
                return null;
        }
    }
}
