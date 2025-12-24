package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Product;

import java.util.List;

public class ProductController {
    private static final int PAGE_SIZE = 20;

    public ProductController() {
        Database.initDatabase();
    }

    public int countProducts() {
        return Database.countProducts();
    }

    public List<Product> getPage(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        return Database.getProducts(offset, PAGE_SIZE);
    }
    
    /**
     * Search products by title
     * @param searchTerm Search term to match against product title
     * @param pageIndex Page index (0-based)
     * @return List of matching products for the given page
     */
    public List<Product> searchProducts(String searchTerm, int pageIndex) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPage(pageIndex);  // Return all products if search is empty
        }
        int offset = pageIndex * PAGE_SIZE;
        return Database.searchProducts(searchTerm.trim(), offset, PAGE_SIZE);
    }
    
    /**
     * Count products matching search term
     * @param searchTerm Search term
     * @return Number of matching products
     */
    public int countSearchResults(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return countProducts();  // Return total count if search is empty
        }
        return Database.countSearchResults(searchTerm.trim());
    }

    public int getPageSize() { return PAGE_SIZE; }
}
