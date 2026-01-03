package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.DeliveryInfoDAO;
import com.hust.soict.aims.entities.DeliveryInfo;

import java.sql.*;

/**
 * DAO Implementation for DeliveryInfo operations
 * Contains SQL queries and database access logic
 */
public class DeliveryInfoDAOImpl extends BaseDAO implements DeliveryInfoDAO {
    
    @Override
    public void insertDeliveryInfo(DeliveryInfo deliveryInfo) throws SQLException {
        String sql = "INSERT INTO DeliveryInfo (order_id, recipient_name, phone_number, email, " +
                     "delivery_address, city, instructions) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, deliveryInfo.getOrderId());
            ps.setString(2, deliveryInfo.getRecipientName());
            ps.setString(3, deliveryInfo.getPhoneNumber());
            ps.setString(4, deliveryInfo.getEmail());
            ps.setString(5, deliveryInfo.getDeliveryAddress());
            ps.setString(6, deliveryInfo.getCity());
            ps.setString(7, deliveryInfo.getInstructions());
            ps.executeUpdate();
        }
    }
    
    @Override
    public DeliveryInfo getDeliveryInfoByOrderId(long orderId) {
        String sql = "SELECT order_id, recipient_name, phone_number, email, delivery_address, city, instructions " +
                     "FROM DeliveryInfo WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DeliveryInfo deliveryInfo = new DeliveryInfo();
                    deliveryInfo.setOrderId(rs.getLong("order_id"));
                    deliveryInfo.setRecipientName(rs.getString("recipient_name"));
                    deliveryInfo.setPhoneNumber(rs.getString("phone_number"));
                    deliveryInfo.setEmail(rs.getString("email"));
                    deliveryInfo.setDeliveryAddress(rs.getString("delivery_address"));
                    deliveryInfo.setCity(rs.getString("city"));
                    deliveryInfo.setInstructions(rs.getString("instructions"));
                    return deliveryInfo;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting delivery info: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean updateDeliveryInfo(DeliveryInfo deliveryInfo) {
        String sql = "UPDATE DeliveryInfo SET recipient_name=?, phone_number=?, email=?, " +
                     "delivery_address=?, city=?, instructions=? WHERE order_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deliveryInfo.getRecipientName());
            ps.setString(2, deliveryInfo.getPhoneNumber());
            ps.setString(3, deliveryInfo.getEmail());
            ps.setString(4, deliveryInfo.getDeliveryAddress());
            ps.setString(5, deliveryInfo.getCity());
            ps.setString(6, deliveryInfo.getInstructions());
            ps.setLong(7, deliveryInfo.getOrderId());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating delivery info: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

