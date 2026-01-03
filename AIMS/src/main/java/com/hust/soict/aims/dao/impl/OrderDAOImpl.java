package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.OrderDAO;
import com.hust.soict.aims.entities.Order;

import java.sql.*;

/**
 * DAO Implementation for Order operations
 * Contains SQL queries and database access logic
 */
public class OrderDAOImpl extends BaseDAO implements OrderDAO {
    
    @Override
    public long insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO Orders (status, created_at) VALUES (?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getStatus() != null ? order.getStatus() : "pending");
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long orderId = rs.getLong(1);
                    order.setOrderId(orderId);
                    
                    // Query back to get the created_at timestamp from database
                    String selectSql = "SELECT created_at FROM Orders WHERE order_id = ?";
                    try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                        selectPs.setLong(1, orderId);
                        try (ResultSet selectRs = selectPs.executeQuery()) {
                            if (selectRs.next()) {
                                Timestamp createdAt = selectRs.getTimestamp("created_at");
                                order.setCreatedAt(createdAt);
                            }
                        }
                    }
                    
                    return orderId;
                }
            }
        }
        throw new SQLException("Failed to insert order");
    }
    
    @Override
    public Order getOrderById(long orderId) {
        String sql = "SELECT order_id, status, processed_by, processed_at, reject_reason, created_at " +
                     "FROM Orders WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getLong("order_id"));
                    order.setStatus(rs.getString("status"));
                    order.setProcessedBy(rs.getObject("processed_by") != null ? rs.getInt("processed_by") : null);
                    order.setProcessedAt(rs.getTimestamp("processed_at"));
                    order.setRejectReason(rs.getString("reject_reason"));
                    order.setCreatedAt(rs.getTimestamp("created_at"));
                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean updateOrderStatus(long orderId, String status) {
        String sql = "UPDATE Orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

