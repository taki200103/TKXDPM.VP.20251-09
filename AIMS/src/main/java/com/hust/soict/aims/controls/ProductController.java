package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.dao.ProductDAO;
import com.hust.soict.aims.dao.impl.ProductDAOImpl;

import java.util.List;

public class ProductController {
    private static final int PAGE_SIZE = 20;
    private ProductDAO productDAO;

    public ProductController() {
        Database.initDatabase();
        this.productDAO = new ProductDAOImpl();
    }

    public int countProducts() {
        return productDAO.countProducts();
    }

    public List<Product> getPage(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        return productDAO.getProducts(offset, PAGE_SIZE);
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
        return productDAO.searchProducts(searchTerm.trim(), offset, PAGE_SIZE);
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
        return productDAO.countSearchResults(searchTerm.trim());
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
        return productDAO.searchProductsWithFilters(
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
        return productDAO.countFilteredResults(
            searchTerm != null ? searchTerm.trim() : "",
            category,
            minPrice,
            maxPrice
        );
    }
    
    /**
     * Get all products for management (including deactivated)
     * @param offset Starting position
     * @param limit Number of records
     * @return List of products
     */
    public List<Product> getAllProductsForManagement(int offset, int limit) {
        return productDAO.getAllProductsForManagement(offset, limit);
    }
    
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
    public List<Product> searchProductsWithFiltersForManagement(String searchTerm, String category,
                                                                Double minPrice, Double maxPrice,
                                                                int offset, int limit) {
        return productDAO.searchProductsWithFiltersForManagement(searchTerm, category, minPrice, maxPrice, offset, limit);
    }
    
    /**
     * Count all products for management (including deactivated)
     * @return Total count
     */
    public int countAllProductsForManagement() {
        return productDAO.countAllProductsForManagement();
    }
    
    /**
     * Get product by ID
     * @param productId Product ID
     * @return Product object or null if not found
     */
    public Product getProductById(long productId) {
        return productDAO.getProductById(productId);
    }
    
    /**
     * Get stock quantity for a product
     * @param productId Product ID
     * @return Stock quantity
     */
    public int getStock(long productId) {
        return productDAO.getStock(productId);
    }
    
    /**
     * Add a new product
     * @param product Product to add
     * @return Generated product ID, or -1 if failed
     */
    public long addProduct(Product product) {
        return productDAO.addProduct(product);
    }
    
    /**
     * Update an existing product
     * @param product Product to update
     * @return true if successful
     */
    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }
    
    /**
     * Delete a product by ID
     * Only deletes if stock = 0, otherwise sets status to 'deactivated'
     * @param productId Product ID
     * @return true if successful
     */
    public boolean deleteProduct(long productId) {
        return productDAO.deleteProduct(productId);
    }

    public int getPageSize() { return PAGE_SIZE; }
}
