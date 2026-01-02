package com.hust.soict.aims.services;

import com.hust.soict.aims.entities.Invoice;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to store payment context (mapping between payment provider order IDs and invoices)
 * This allows PayPal callbacks to process orders even though they don't have direct access to Invoice
 */
public class PaymentContextService {
    private static PaymentContextService instance;
    private final Map<String, Invoice> paypalOrderToInvoice = new ConcurrentHashMap<>();
    private final Map<String, Invoice> qrTransactionToInvoice = new ConcurrentHashMap<>();

    private PaymentContextService() {}

    public static synchronized PaymentContextService getInstance() {
        if (instance == null) {
            instance = new PaymentContextService();
        }
        return instance;
    }

    /**
     * Store mapping between PayPal order ID and Invoice
     */
    public void storePayPalOrder(String paypalOrderId, Invoice invoice) {
        if (paypalOrderId != null && invoice != null) {
            paypalOrderToInvoice.put(paypalOrderId, invoice);
            System.out.println("[PaymentContextService] Stored PayPal order mapping: " + paypalOrderId);
        }
    }

    /**
     * Get Invoice for PayPal order ID
     */
    public Invoice getInvoiceForPayPalOrder(String paypalOrderId) {
        Invoice invoice = paypalOrderToInvoice.get(paypalOrderId);
        if (invoice != null) {
            System.out.println("[PaymentContextService] Found invoice for PayPal order: " + paypalOrderId);
        } else {
            System.err.println("[PaymentContextService] No invoice found for PayPal order: " + paypalOrderId);
        }
        return invoice;
    }

    /**
     * Remove PayPal order mapping after processing
     */
    public void removePayPalOrder(String paypalOrderId) {
        paypalOrderToInvoice.remove(paypalOrderId);
    }

    /**
     * Store mapping between QR transaction and Invoice
     */
    public void storeQRTransaction(String transactionNo, Invoice invoice) {
        if (transactionNo != null && invoice != null) {
            qrTransactionToInvoice.put(transactionNo, invoice);
        }
    }

    /**
     * Get Invoice for QR transaction
     */
    public Invoice getInvoiceForQRTransaction(String transactionNo) {
        return qrTransactionToInvoice.get(transactionNo);
    }

    /**
     * Remove QR transaction mapping after processing
     */
    public void removeQRTransaction(String transactionNo) {
        qrTransactionToInvoice.remove(transactionNo);
    }
}

