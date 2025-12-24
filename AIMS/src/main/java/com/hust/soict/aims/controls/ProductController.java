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
    
    /**
     * Search and filter products with multiple criteria
     * @param searchTerm Search term for title (can be empty)
     * @param category Product category/type (can be null or "all" for all)
     * @param minPrice Minimum price in VND (can be null)
     * @param maxPrice Maximum price in VND (can be null)
     * @param pageIndex Page index (0-based)
     * @return List of matching products for the given page
     */
    public List<Product> searchProductsWithFilters(String searchTerm, String category, Double minPrice, Double maxPrice, int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        return Database.searchProductsWithFilters(
            searchTerm != null ? searchTerm.trim() : "",
            category,
            minPrice,
            maxPrice,
            offset,
            PAGE_SIZE
        );
    }
    
    /**
     * Count products matching filter criteria
     * @param searchTerm Search term for title (can be empty)
     * @param category Product category/type (can be null or "all" for all)
     * @param minPrice Minimum price in VND (can be null)
     * @param maxPrice Maximum price in VND (can be null)
     * @return Number of matching products
     */
    public int countFilteredResults(String searchTerm, String category, Double minPrice, Double maxPrice) {
        return Database.countFilteredResults(
            searchTerm != null ? searchTerm.trim() : "",
            category,
            minPrice,
            maxPrice
        );
    }

    public int getPageSize() { return PAGE_SIZE; }
}
