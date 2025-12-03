package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.deliveryInfo.DeliveryInfo;

import java.util.List;
import java.util.Optional;
import java.sql.*;

public class DeliveryInfoDao implements Dao<DeliveryInfo>{
    @Override
    public List<DeliveryInfo> getAll() {
        return null;
    }

    @Override
    public Optional<DeliveryInfo> get(int id) {
        return Optional.empty();
    }

    @Override
    public void save(DeliveryInfo info) throws SQLException {
        String deliveryInfoSQL = "INSERT INTO DeliveryInfo (delivery_address, city, recipient_name, email, phone_number, instructions) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBconnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(deliveryInfoSQL)) {
            // Gán giá trị từ đối tượng DeliveryInfo vào câu lệnh SQL
            stmt.setString(1, info.getDeliveryAddress() != null ? info.getDeliveryAddress() : "Unknown Address");
            stmt.setString(2, info.getCity() != null ? info.getCity() : "Unknown City");
            stmt.setString(3, info.getRecipientName() != null ? info.getRecipientName() : "Unknown Recipient");
            stmt.setString(4, info.getEmail() != null ? info.getEmail() : "unknown@example.com");
            stmt.setString(5, info.getPhoneNumber() != null ? info.getPhoneNumber() : "0000000000");
            stmt.setString(6, info.getInstructions() != null ? info.getInstructions() : "");

            // Thực thi câu lệnh INSERT
            stmt.executeUpdate();

            // Lấy ID tự động sinh từ last_insert_rowid() của SQLite
            try (Statement stmtId = conn.createStatement()) {
                ResultSet rs = stmtId.executeQuery("SELECT last_insert_rowid()");
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    info.setId(generatedId);
                } else {
                    throw new SQLException("Failed to retrieve DeliveryInfo ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving delivery info: " + e.getMessage(), e);
        }
    }



    @Override
    public void update(DeliveryInfo info) {

    }

    @Override
    public void delete(DeliveryInfo info) {

    }
}
