package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.DeliveryInfo;

import java.sql.SQLException;

/**
 * DAO Interface for DeliveryInfo operations
 * Defines contract for DeliveryInfo data access operations
 */
public interface DeliveryInfoDAO {
    
    /**
     * Insert delivery information for an order
     * @param deliveryInfo DeliveryInfo object
     * @throws SQLException if insertion fails
     */
    void insertDeliveryInfo(DeliveryInfo deliveryInfo) throws SQLException;
    
    /**
     * Get delivery info by order ID
     * @param orderId Order ID
     * @return DeliveryInfo object or null if not found
     */
    DeliveryInfo getDeliveryInfoByOrderId(long orderId);
    
    /**
     * Update delivery information
     * @param deliveryInfo DeliveryInfo object
     * @return true if successful
     */
    boolean updateDeliveryInfo(DeliveryInfo deliveryInfo);
}
