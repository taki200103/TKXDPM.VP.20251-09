package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Product;

import java.util.List;

/**
 * DAO Interface for Product operations
 * Defines contract for Product data access operations
 */
public interface ProductDAO {
    
    /**
     * Get products with pagination
     * @param offset Starting position
     * @param limit Number of records
     * @return List of products
     */
    List<Product> getProducts(int offset, int limit);
    
    /**
     * Get product by ID
     * @param productId Product ID
     * @return Product object or null if not found
     */
    Product getProductById(long productId);
    
    /**
     * Get stock quantity for a product
     * @param productId Product ID
     * @return Stock quantity
     */
    int getStock(long productId);
    
    /**
     * Reduce stock for a product
     * @param productId Product ID
     * @param amount Amount to reduce
     * @return true if successful, false otherwise
     */
    boolean reduceStock(long productId, int amount);
    
    /**
     * Count total products
     * @return Total count
     */
    int countProducts();
    
    /**
     * Search products by title
     * @param searchTerm Search term
     * @param offset Starting position
     * @param limit Number of records
     * @return List of matching products
     */
    List<Product> searchProducts(String searchTerm, int offset, int limit);
    
    /**
     * Count products matching search term
     * @param searchTerm Search term
     * @return Number of matching products
     */
    int countSearchResults(String searchTerm);
    
    /**
     * Search and filter products with multiple criteria
     * @param searchTerm Search term for title (can be empty)
     * @param category Product category/type (can be null or "all" for all)
     * @param minPrice Minimum price in VND (can be null)
     * @param maxPrice Maximum price in VND (can be null)
     * @param offset Starting position
     * @param limit Number of records
     * @return List of matching products
     */
    List<Product> searchProductsWithFilters(String searchTerm, String category, 
                                           Double minPrice, Double maxPrice, 
                                           int offset, int limit);
    
    /**
     * Count products matching filter criteria
     * @param searchTerm Search term for title (can be empty)
     * @param category Product category/type (can be null or "all" for all)
     * @param minPrice Minimum price in VND (can be null)
     * @param maxPrice Maximum price in VND (can be null)
     * @return Number of matching products
     */
    int countFilteredResults(String searchTerm, String category, 
                            Double minPrice, Double maxPrice);
    
    /**
     * Get all products for management (including deactivated)
     * @param offset Starting position
     * @param limit Number of records
     * @return List of products
     */
    List<Product> getAllProductsForManagement(int offset, int limit);
    
    /**
     * Search products with filters for management (including deactivated)
     * @param searchTerm Search term for title (can be empty)
     * @param category Product category/type (can be null or "all" for all)
     * @param minPrice Minimum price in VND (can be null)
     * @param maxPrice Maximum price in VND (can be null)
     * @param offset Starting position
     * @param limit Number of records
     * @return List of matching products
     */
    List<Product> searchProductsWithFiltersForManagement(String searchTerm, String category,
                                                        Double minPrice, Double maxPrice,
                                                        int offset, int limit);
    
    /**
     * Count all products for management (including deactivated)
     * @return Total count
     */
    int countAllProductsForManagement();
    
    /**
     * Add a new product
     * @param product Product to add
     * @return Generated product ID, or -1 if failed
     */
    long addProduct(Product product);
    
    /**
     * Update an existing product
     * @param product Product to update
     * @return true if successful
     */
    boolean updateProduct(Product product);
    
    /**
     * Delete a product by ID
     * Only deletes if stock = 0, otherwise sets status to 'deactivated'
     * @param productId Product ID
     * @return true if successful
     */
    boolean deleteProduct(long productId);
    
    /**
     * Update product status
     * @param productId Product ID
     * @param status New status
     * @return true if successful
     */
    boolean updateProductStatus(long productId, String status);
}
