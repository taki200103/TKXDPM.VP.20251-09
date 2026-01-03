package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.OrderMedia;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO Interface for OrderMedia operations
 * Defines contract for OrderMedia data access operations
 */
public interface OrderMediaDAO {
    
    /**
     * Insert a single order media item
     * @param orderMedia OrderMedia object
     * @throws SQLException if insertion fails
     */
    void insertOrderMedia(OrderMedia orderMedia) throws SQLException;
    
    /**
     * Insert multiple order media items in batch
     * @param orderMediaList List of OrderMedia objects
     * @throws SQLException if insertion fails
     */
    void insertOrderMediaBatch(List<OrderMedia> orderMediaList) throws SQLException;
    
    /**
     * Get all order media items for an order
     * @param orderId Order ID
     * @return List of OrderMedia objects
     */
    List<OrderMedia> getOrderMediaByOrderId(long orderId);
}
