package com.hust.soict.aims.controls;

import com.hust.soict.aims.dao.DatabaseInitializer;
import com.hust.soict.aims.dao.DatabaseSeeder;

import java.sql.SQLException;

/**
 * Database Configuration and Initialization
 * 
 * This class now only contains:
 * - Database connection constants
 * - initDatabase() method that delegates to specialized classes
 * 
 * All CRUD operations have been moved to DAO classes.
 * All initialization logic has been moved to DatabaseInitializer.
 * All migration logic has been moved to DatabaseMigrator.
 * All seeding logic has been moved to DatabaseSeeder.
 * 
 * Legacy static methods are available in DatabaseLegacy class for backward compatibility.
 */
public class Database {
    public static final String DB_FILE = "aims.db";
    public static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    /**
     * Initialize database
     * This method:
     * 1. Creates all tables and default data (via DatabaseInitializer)
     * 2. Migrates legacy data if needed (via DatabaseMigrator)
     * 3. Seeds sample data if Media table is empty (via DatabaseSeeder)
     */
    public static void initDatabase() {
        try {
            // Step 1: Initialize database schema and default data
            DatabaseInitializer.initialize();   
            // Step 2: Seed sample data if Media table is empty
            DatabaseSeeder.seed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // =======================
    // Legacy Static Methods (delegated to DatabaseLegacy for backward compatibility)
    // =======================
    
    /**
     * @deprecated Use ProductDAO.searchProducts() instead
     */
    @Deprecated
    public static java.util.List<com.hust.soict.aims.entities.Product> searchProducts(String searchTerm, int offset, int limit) {
        return DatabaseLegacy.searchProducts(searchTerm, offset, limit);
    }
    
    /**
     * @deprecated Use ProductDAO.countSearchResults() instead
     */
    @Deprecated
    public static int countSearchResults(String searchTerm) {
        return DatabaseLegacy.countSearchResults(searchTerm);
    }
    
    /**
     * @deprecated Use ProductDAO.searchProductsWithFilters() instead
     */
    @Deprecated
    public static java.util.List<com.hust.soict.aims.entities.Product> searchProductsWithFilters(String searchTerm, String category, 
                                                                                                  Double minPrice, Double maxPrice, 
                                                                                                  int offset, int limit) {
        return DatabaseLegacy.searchProductsWithFilters(searchTerm, category, minPrice, maxPrice, offset, limit);
    }
    
    /**
     * @deprecated Use ProductDAO.countFilteredResults() instead
     */
    @Deprecated
    public static int countFilteredResults(String searchTerm, String category, 
                                          Double minPrice, Double maxPrice) {
        return DatabaseLegacy.countFilteredResults(searchTerm, category, minPrice, maxPrice);
    }
    
    /**
     * @deprecated Use ProductDAO.getAllProductsForManagement() instead
     */
    @Deprecated
    public static java.util.List<com.hust.soict.aims.entities.Product> getAllProductsForManagement(int offset, int limit) {
        return DatabaseLegacy.getAllProductsForManagement(offset, limit);
    }
    
    /**
     * @deprecated Use ProductDAO.searchProductsWithFiltersForManagement() instead
     */
    @Deprecated
    public static java.util.List<com.hust.soict.aims.entities.Product> searchProductsWithFiltersForManagement(String searchTerm, String category,
                                                                                                               Double minPrice, Double maxPrice,
                                                                                                               int offset, int limit) {
        return DatabaseLegacy.searchProductsWithFiltersForManagement(searchTerm, category, minPrice, maxPrice, offset, limit);
    }
    
    /**
     * @deprecated Use ProductDAO.countAllProductsForManagement() instead
     */
    @Deprecated
    public static int countAllProductsForManagement() {
        return DatabaseLegacy.countAllProductsForManagement();
    }
    
    /**
     * @deprecated Use ProductDAO.addProduct() instead
     */
    @Deprecated
    public static long addProduct(com.hust.soict.aims.entities.Product product) {
        return DatabaseLegacy.addProduct(product);
    }
    
    /**
     * @deprecated Use ProductDAO.updateProduct() instead
     */
    @Deprecated
    public static boolean updateProduct(com.hust.soict.aims.entities.Product product) {
        return DatabaseLegacy.updateProduct(product);
    }
    
    /**
     * @deprecated Use ProductDAO.deleteProduct() instead
     */
    @Deprecated
    public static boolean deleteProduct(long productId) {
        return DatabaseLegacy.deleteProduct(productId);
    }
    
    /**
     * @deprecated Use ProductDAO.updateProductStatus() instead
     */
    @Deprecated
    public static boolean updateProductStatus(long productId, String status) {
        return DatabaseLegacy.updateProductStatus(productId, status);
    }
    
    /**
     * @deprecated Use TrackDAO.loadTracks() instead
     */
    @Deprecated
    public static java.util.List<com.hust.soict.aims.entities.Track> loadTracks(long mediaId) {
        return DatabaseLegacy.loadTracks(mediaId);
    }
    
    /**
     * @deprecated Use TrackDAO.saveTracks() instead
     */
    @Deprecated
    public static boolean saveTracks(long mediaId, java.util.List<com.hust.soict.aims.entities.Track> tracks) {
        return DatabaseLegacy.saveTracks(mediaId, tracks);
    }
    
    /**
     * @deprecated Use OrderDAO.insertOrder() instead
     */
    @Deprecated
    public static long insertOrder(com.hust.soict.aims.entities.Order order) throws SQLException {
        return DatabaseLegacy.insertOrder(order);
    }
    
    /**
     * @deprecated Use DeliveryInfoDAO.insertDeliveryInfo() instead
     */
    @Deprecated
    public static void insertDeliveryInfo(com.hust.soict.aims.entities.DeliveryInfo deliveryInfo) throws SQLException {
        DatabaseLegacy.insertDeliveryInfo(deliveryInfo);
    }
    
    /**
     * @deprecated Use OrderMediaDAO.insertOrderMedia() instead
     */
    @Deprecated
    public static void insertOrderMedia(com.hust.soict.aims.entities.OrderMedia orderMedia) throws SQLException {
        DatabaseLegacy.insertOrderMedia(orderMedia);
    }
    
    /**
     * @deprecated Use OrderMediaDAO.insertOrderMediaBatch() instead
     */
    @Deprecated
    public static void insertOrderMediaBatch(java.util.List<com.hust.soict.aims.entities.OrderMedia> orderMediaList) throws SQLException {
        DatabaseLegacy.insertOrderMediaBatch(orderMediaList);
    }
    
    /**
     * @deprecated Use PaymentTransactionDAO.insertPaymentTransaction() instead
     */
    @Deprecated
    public static long insertPaymentTransaction(com.hust.soict.aims.entities.PaymentTransaction paymentTransaction) throws SQLException {
        return DatabaseLegacy.insertPaymentTransaction(paymentTransaction);
    }
    
    /**
     * @deprecated Use InvoiceDAO.insertInvoice() instead
     */
    @Deprecated
    public static long insertInvoice(com.hust.soict.aims.entities.Invoice invoice) throws SQLException {
        return DatabaseLegacy.insertInvoice(invoice);
    }
    
    /**
     * @deprecated Use ProductDAO.getStock() instead
     */
    @Deprecated
    public static int getStock(long productId) {
        return DatabaseLegacy.getStock(productId);
    }
    
    /**
     * @deprecated Use ProductDAO.reduceStock() instead
     */
    @Deprecated
    public static boolean reduceStock(long productId, int amount) {
        return DatabaseLegacy.reduceStock(productId, amount);
    }
    
    /**
     * @deprecated Use ProductDAO.countProducts() instead
     */
    @Deprecated
    public static int countProducts() {
        return DatabaseLegacy.countProducts();
    }
    
    /**
     * @deprecated Use ProductDAO.getProductById() instead
     */
    @Deprecated
    public static com.hust.soict.aims.entities.Product getProductById(long productId) {
        return DatabaseLegacy.getProductById(productId);
    }
    
    /**
     * Map Media row to Product object
     * @deprecated Use ProductMapper.mapMediaToProduct() instead
     * This method is kept for backward compatibility
     */
    @Deprecated
    public static com.hust.soict.aims.entities.Product mapMediaToProduct(java.sql.Connection conn, java.sql.ResultSet rs) throws SQLException {
        return com.hust.soict.aims.utils.ProductMapper.mapMediaToProduct(conn, rs);
    }
}
