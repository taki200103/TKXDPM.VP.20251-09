package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.PaymentTransaction;

import java.sql.SQLException;

/**
 * DAO Interface for PaymentTransaction operations
 * Defines contract for PaymentTransaction data access operations
 */
public interface PaymentTransactionDAO {
    
    /**
     * Insert a new payment transaction
     * @param paymentTransaction PaymentTransaction object
     * @return The generated payment_transaction_id
     * @throws SQLException if insertion fails
     */
    long insertPaymentTransaction(PaymentTransaction paymentTransaction) throws SQLException;
    
    /**
     * Get payment transaction by ID
     * @param paymentTransactionId Payment Transaction ID
     * @return PaymentTransaction object or null if not found
     */
    PaymentTransaction getPaymentTransactionById(long paymentTransactionId);
}
