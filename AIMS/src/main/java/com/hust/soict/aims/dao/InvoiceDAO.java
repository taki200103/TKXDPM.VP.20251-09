package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Invoice;

import java.sql.SQLException;

/**
 * DAO Interface for Invoice operations
 * Defines contract for Invoice data access operations
 */
public interface InvoiceDAO {
    
    /**
     * Insert a new invoice
     * @param invoice Invoice object
     * @return The generated invoice_id
     * @throws SQLException if insertion fails
     */
    long insertInvoice(Invoice invoice) throws SQLException;
    
    /**
     * Get invoice by ID
     * @param invoiceId Invoice ID
     * @return Invoice object or null if not found
     */
    Invoice getInvoiceById(long invoiceId);
    
    /**
     * Get invoice by order ID
     * @param orderId Order ID
     * @return Invoice object or null if not found
     */
    Invoice getInvoiceByOrderId(long orderId);
}
