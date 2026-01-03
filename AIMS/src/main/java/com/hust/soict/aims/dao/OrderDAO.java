package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Order;

import java.sql.SQLException;

/**
 * DAO Interface for Order operations
 * Defines contract for Order data access operations
 */
public interface OrderDAO {
    
    /**
     * Insert a new order
     * @param order Order object
     * @return The generated order_id
     * @throws SQLException if insertion fails
     */
    long insertOrder(Order order) throws SQLException;
    
    /**
     * Get order by ID
     * @param orderId Order ID
     * @return Order object or null if not found
     */
    Order getOrderById(long orderId);
    
    /**
     * Update order status
     * @param orderId Order ID
     * @param status New status
     * @return true if successful
     */
    boolean updateOrderStatus(long orderId, String status);
}
