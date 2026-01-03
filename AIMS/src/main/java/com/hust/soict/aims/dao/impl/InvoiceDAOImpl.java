package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.InvoiceDAO;
import com.hust.soict.aims.entities.Invoice;

import java.sql.*;

/**
 * DAO Implementation for Invoice operations
 * Contains SQL queries and database access logic
 */
public class InvoiceDAOImpl extends BaseDAO implements InvoiceDAO {
    
    @Override
    public long insertInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Invoice (order_id, payment_transaction_id, product_total, vat_amount, " +
                     "shipping_fee, total_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, invoice.getOrderId());
            ps.setLong(2, invoice.getPaymentTransactionId());
            ps.setDouble(3, invoice.getProductTotal());
            ps.setDouble(4, invoice.getVatAmount());
            ps.setDouble(5, invoice.getShippingFee());
            ps.setDouble(6, invoice.getTotalAmount());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long invoiceId = rs.getLong(1);
                    invoice.setInvoiceId(invoiceId);
                    return invoiceId;
                }
            }
        }
        throw new SQLException("Failed to insert invoice");
    }
    
    @Override
    public Invoice getInvoiceById(long invoiceId) {
        String sql = "SELECT invoice_id, order_id, payment_transaction_id, product_total, vat_amount, " +
                     "shipping_fee, total_amount, created_at FROM Invoice WHERE invoice_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getLong("invoice_id"));
                    invoice.setOrderId(rs.getLong("order_id"));
                    invoice.setPaymentTransactionId(rs.getLong("payment_transaction_id"));
                    invoice.setProductTotal(rs.getDouble("product_total"));
                    invoice.setVatAmount(rs.getDouble("vat_amount"));
                    invoice.setShippingFee(rs.getDouble("shipping_fee"));
                    invoice.setTotalAmount(rs.getDouble("total_amount"));
                    invoice.setCreatedAt(rs.getTimestamp("created_at"));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting invoice: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Invoice getInvoiceByOrderId(long orderId) {
        String sql = "SELECT invoice_id, order_id, payment_transaction_id, product_total, vat_amount, " +
                     "shipping_fee, total_amount, created_at FROM Invoice WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getLong("invoice_id"));
                    invoice.setOrderId(rs.getLong("order_id"));
                    invoice.setPaymentTransactionId(rs.getLong("payment_transaction_id"));
                    invoice.setProductTotal(rs.getDouble("product_total"));
                    invoice.setVatAmount(rs.getDouble("vat_amount"));
                    invoice.setShippingFee(rs.getDouble("shipping_fee"));
                    invoice.setTotalAmount(rs.getDouble("total_amount"));
                    invoice.setCreatedAt(rs.getTimestamp("created_at"));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting invoice by order ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

