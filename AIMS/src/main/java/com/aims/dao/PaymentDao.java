package com.aims.dao;   

import com.aims.entity.db.DBconnection;
import com.aims.entity.payment.PaymentTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class PaymentDao implements Dao<PaymentTransaction> {

    @Override
    public List<PaymentTransaction> getAll() throws SQLException {
        String query = "SELECT payment_transaction_id, amount, transaction_no, bank_code, bank_transaction_no, " +
                "card_type, method_type, pay_date, transaction_content FROM PaymentTransaction";
        Connection conn = DBconnection.getConnection();
        List<PaymentTransaction> paymentTransactions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                paymentTransactions.add(mapPaymentTransaction(rs));
            }
        }

        return paymentTransactions;
    }

    @Override
    public Optional<PaymentTransaction> get(int id) throws SQLException {
        String query = "SELECT payment_transaction_id, amount, transaction_no, bank_code, bank_transaction_no, " +
                "card_type, method_type, pay_date, transaction_content " +
                "FROM PaymentTransaction WHERE payment_transaction_id = ?";
        Connection conn = DBconnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaymentTransaction(rs));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public void save(PaymentTransaction paymentTransaction) throws SQLException {
        String paymentTransactionQuery = "INSERT INTO PaymentTransaction (amount, transaction_no, bank_code, " +
                "bank_transaction_no, card_type, method_type, pay_date, transaction_content) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBconnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(paymentTransactionQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, paymentTransaction.getAmount());
            stmt.setString(2, paymentTransaction.getTransactionNo());
            stmt.setString(3, paymentTransaction.getBankCode());
            stmt.setString(4, paymentTransaction.getBankTransNo());
            stmt.setString(5, paymentTransaction.getCardType());
            stmt.setString(6, paymentTransaction.getMethodType());
            stmt.setTimestamp(7, paymentTransaction.getPayDate());
            stmt.setString(8, paymentTransaction.getTransactionContent());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    paymentTransaction.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve PaymentTransaction ID");
                }
            }
        }
    }


    @Override
    public void update(PaymentTransaction paymentTransaction) throws SQLException {
        String query = "UPDATE PaymentTransaction SET amount = ?, transaction_no = ?, bank_code = ?, " +
                "bank_transaction_no = ?, card_type = ?, method_type = ?, pay_date = ?, transaction_content = ? " +
                "WHERE payment_transaction_id = ?";
        Connection conn = DBconnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, paymentTransaction.getAmount());
            stmt.setString(2, paymentTransaction.getTransactionNo());
            stmt.setString(3, paymentTransaction.getBankCode());
            stmt.setString(4, paymentTransaction.getBankTransNo());
            stmt.setString(5, paymentTransaction.getCardType());
            stmt.setString(6, paymentTransaction.getMethodType());
            stmt.setTimestamp(7, paymentTransaction.getPayDate());
            stmt.setString(8, paymentTransaction.getTransactionContent());
            stmt.setInt(9, paymentTransaction.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("No payment transaction found with id " + paymentTransaction.getId());
            }
        }
    }

    @Override
    public void delete(PaymentTransaction paymentTransaction) {
        if (paymentTransaction == null || paymentTransaction.getId() == 0) {
            throw new IllegalArgumentException("PaymentTransaction must have a valid id before deletion");
        }

        String query = "DELETE FROM PaymentTransaction WHERE payment_transaction_id = ?";

        try (PreparedStatement stmt = DBconnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, paymentTransaction.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete payment transaction with id " + paymentTransaction.getId(), e);
        }
    }

    private PaymentTransaction mapPaymentTransaction(ResultSet rs) throws SQLException {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setId(rs.getInt("payment_transaction_id"));
        paymentTransaction.setAmount(rs.getDouble("amount"));
        paymentTransaction.setTransactionNo(rs.getString("transaction_no"));
        paymentTransaction.setBankCode(rs.getString("bank_code"));
        paymentTransaction.setBankTransNo(rs.getString("bank_transaction_no"));
        paymentTransaction.setCardType(rs.getString("card_type"));
        paymentTransaction.setMethodType(rs.getString("method_type"));
        paymentTransaction.setPayDate(rs.getTimestamp("pay_date"));
        paymentTransaction.setTransactionContent(rs.getString("transaction_content"));
        return paymentTransaction;
    }
}
