package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.PaymentTransactionDAO;
import com.hust.soict.aims.entities.PaymentTransaction;

import java.sql.*;

/**
 * DAO Implementation for PaymentTransaction operations
 * Contains SQL queries and database access logic
 */
public class PaymentTransactionDAOImpl extends BaseDAO implements PaymentTransactionDAO {
    
    @Override
    public long insertPaymentTransaction(PaymentTransaction paymentTransaction) throws SQLException {
        String sql = "INSERT INTO PaymentTransaction (amount, method_type, transaction_no, transaction_content, " +
                     "pay_date, bank_code, bank_transaction_no, card_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, paymentTransaction.getAmount());
            ps.setString(2, paymentTransaction.getMethodType());
            ps.setString(3, paymentTransaction.getTransactionNo());
            ps.setString(4, paymentTransaction.getTransactionContent());
            ps.setTimestamp(5, paymentTransaction.getPayDate());
            ps.setString(6, paymentTransaction.getBankCode());
            ps.setString(7, paymentTransaction.getBankTransactionNo());
            ps.setString(8, paymentTransaction.getCardType());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long paymentTransactionId = rs.getLong(1);
                    paymentTransaction.setPaymentTransactionId(paymentTransactionId);
                    return paymentTransactionId;
                }
            }
        }
        throw new SQLException("Failed to insert payment transaction");
    }
    
    @Override
    public PaymentTransaction getPaymentTransactionById(long paymentTransactionId) {
        String sql = "SELECT payment_transaction_id, amount, method_type, transaction_no, transaction_content, " +
                     "pay_date, bank_code, bank_transaction_no, card_type " +
                     "FROM PaymentTransaction WHERE payment_transaction_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, paymentTransactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PaymentTransaction pt = new PaymentTransaction();
                    pt.setPaymentTransactionId(rs.getLong("payment_transaction_id"));
                    pt.setAmount(rs.getDouble("amount"));
                    pt.setMethodType(rs.getString("method_type"));
                    pt.setTransactionNo(rs.getString("transaction_no"));
                    pt.setTransactionContent(rs.getString("transaction_content"));
                    pt.setPayDate(rs.getTimestamp("pay_date"));
                    pt.setBankCode(rs.getString("bank_code"));
                    pt.setBankTransactionNo(rs.getString("bank_transaction_no"));
                    pt.setCardType(rs.getString("card_type"));
                    return pt;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting payment transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

