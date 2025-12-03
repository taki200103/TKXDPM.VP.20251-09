package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.deliveryInfo.DeliveryInfo;
import com.aims.entity.media.Media;
import com.aims.entity.order.Order;
import com.aims.entity.order.OrderMedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class OrderDao implements Dao<Order> {
    public OrderDao() {
    }

    @Override
    public List<Order> getAll() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Order> get(int id) throws SQLException {
        String sql = "SELECT o.*, d.*, om.*, m.* FROM Orders o " +
                "JOIN DeliveryInfo d ON o.delivery_info_id = d.delivery_info_id " +
                "JOIN OrderMedia om ON o.order_id = om.order_id " +
                "JOIN Media m ON om.media_id = m.media_id " +
                "WHERE o.order_id = ?";

        try (PreparedStatement pst = DBconnection.getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            Order order = null;
            while (rs.next()) {
                if (order == null) {
                    order = new Order();
                    order.setId(rs.getInt("order_id"));
                    order.setStatus(rs.getString("status"));

                    DeliveryInfo deliveryInfo = new DeliveryInfo(
                            rs.getString("delivery_address"),
                            rs.getString("city"),
                            rs.getString("recipient_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("instructions")
                    );
                    order.setDeliveryInfo(deliveryInfo);
                }

                Media media = new Media(
                        rs.getInt("media_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_url"),
                        rs.getString("description"),
                        rs.getDouble("weight")
                );

                OrderMedia orderMedia = new OrderMedia(
                        media,
                        rs.getInt("quantity"),
                        rs.getInt("price")
                );

                order.addOrderMedia(orderMedia);
                order.setShippingFees();
            }

            return Optional.ofNullable(order);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void save(Order order) throws SQLException {
        String orderSQL = "INSERT INTO Orders (delivery_info_id, shipping_fee, status) VALUES (?, ?, ?)";
        String orderMediaSQL = "INSERT INTO OrderMedia (order_id, media_id, quantity) VALUES (?, ?, ?)";
        Connection conn = DBconnection.getConnection();
        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            int orderId;
            // Thêm Order và lấy ID
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSQL)) {
                orderStmt.setInt(1, order.getDeliveryInfo().getId());
                orderStmt.setDouble(2, order.getShippingFees());
                orderStmt.setString(3, order.getStatus());

                orderStmt.executeUpdate();

                // Lấy ID vừa chèn
                try (Statement stmtId = conn.createStatement()) {
                    ResultSet rs = stmtId.executeQuery("SELECT last_insert_rowid()"); // Dùng SQLite riêng để lấy ID
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                        order.setId(orderId);
                    } else {
                        throw new SQLException("Failed to retrieve Order ID");
                    }
                }
            }

            // Thêm OrderMedia
            try (PreparedStatement orderMediaStmt = conn.prepareStatement(orderMediaSQL)) {
                for (OrderMedia orderMedia : order.getlistOrderMedia()) {
                    Media media = orderMedia.getMedia();

                    orderMediaStmt.setInt(1, order.getId()); // ID của Order
                    orderMediaStmt.setInt(2, media != null ? media.getId() : 0); // ID của Media
                    orderMediaStmt.setInt(3, orderMedia.getQuantity() > 0 ? orderMedia.getQuantity() : 1); // Số lượng

                    orderMediaStmt.addBatch(); // Thêm vào batch
                }
                orderMediaStmt.executeBatch(); // Thực thi batch
            }

            conn.commit(); // Commit transaction nếu mọi thứ thành công
        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback nếu có lỗi
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Error while saving order and order media: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true); // Kết thúc transaction
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Order order) throws SQLException {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement pst = DBconnection.getConnection().prepareStatement(sql)) {
            // Cập nhật trạng thái đơn hàng
            pst.setString(1, order.getStatus());  // Trạng thái mới
            pst.setInt(2, order.getId());         // ID đơn hàng

            // Thực thi câu lệnh UPDATE
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("No order found with the given ID or failed to update status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Ném lại ngoại lệ để caller xử lý
        }
    }

    @Override
    public void delete(Order order) {

    }
}
