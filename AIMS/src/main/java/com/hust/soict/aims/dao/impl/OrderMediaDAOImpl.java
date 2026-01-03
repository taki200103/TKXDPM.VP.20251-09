package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.OrderMediaDAO;
import com.hust.soict.aims.entities.OrderMedia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Implementation for OrderMedia operations
 * Contains SQL queries and database access logic
 */
public class OrderMediaDAOImpl extends BaseDAO implements OrderMediaDAO {
    
    @Override
    public void insertOrderMedia(OrderMedia orderMedia) throws SQLException {
        String sql = "INSERT INTO OrderMedia (order_id, media_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderMedia.getOrderId());
            ps.setLong(2, orderMedia.getMediaId());
            ps.setInt(3, orderMedia.getQuantity());
            ps.setDouble(4, orderMedia.getPrice());
            ps.executeUpdate();
        }
    }
    
    @Override
    public void insertOrderMediaBatch(List<OrderMedia> orderMediaList) throws SQLException {
        String sql = "INSERT INTO OrderMedia (order_id, media_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderMedia orderMedia : orderMediaList) {
                ps.setLong(1, orderMedia.getOrderId());
                ps.setLong(2, orderMedia.getMediaId());
                ps.setInt(3, orderMedia.getQuantity());
                ps.setDouble(4, orderMedia.getPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    @Override
    public List<OrderMedia> getOrderMediaByOrderId(long orderId) {
        List<OrderMedia> list = new ArrayList<>();
        String sql = "SELECT order_id, media_id, quantity, price FROM OrderMedia WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderMedia orderMedia = new OrderMedia();
                    orderMedia.setOrderId(rs.getLong("order_id"));
                    orderMedia.setMediaId(rs.getLong("media_id"));
                    orderMedia.setQuantity(rs.getInt("quantity"));
                    orderMedia.setPrice(rs.getDouble("price"));
                    list.add(orderMedia);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order media: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}

