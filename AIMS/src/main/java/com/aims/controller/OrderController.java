package com.aims.controller;

import com.aims.dao.OrderDao;
import com.aims.entity.order.Order;
import com.aims.entity.db.DBconnection;
import com.aims.views.popup.PopupForm;

import java.sql.SQLException;
import java.util.Optional;

public class OrderController {

    public Optional<Order> getOrderDetails(int orderID) throws SQLException {
        return new OrderDao().get(orderID);
    }

    /**
     * Yêu cầu hủy đơn hàng.
     * Lưu ý: phần thanh toán/hoàn tiền chưa được triển khai, nên hiện tại chỉ hiển thị thông báo.
     */
    public void requestToCancelOrder(int orderID) throws SQLException {
        Optional<Order> optionalOrder = new OrderDao().get(orderID);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if ("Processing".equalsIgnoreCase(order.getStatus())) {
                // TODO: triển khai logic hoàn tiền/hủy thanh toán sau
                PopupForm.error("Chức năng hủy đơn & hoàn tiền hiện chưa được triển khai.");
            } else {
                PopupForm.error("Only orders with status 'Processing' can be cancelled.");
            }
        } else {
            PopupForm.error("Order not found.");
        }
    }
}
