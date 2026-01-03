package com.hust.soict.aims.services;

import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.dao.*;
import com.hust.soict.aims.dao.impl.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for processing completed orders after successful payment
 * Handles database inserts and stock reduction
 */
public class OrderService {
    private EmailService emailService;
    private OrderDAO orderDAO;
    private DeliveryInfoDAO deliveryInfoDAO;
    private OrderMediaDAO orderMediaDAO;
    private PaymentTransactionDAO paymentTransactionDAO;
    private InvoiceDAO invoiceDAO;
    private ProductDAO productDAO;

    public OrderService() {
        this.emailService = EmailService.getInstance();
        this.orderDAO = new OrderDAOImpl();
        this.deliveryInfoDAO = new DeliveryInfoDAOImpl();
        this.orderMediaDAO = new OrderMediaDAOImpl();
        this.paymentTransactionDAO = new PaymentTransactionDAOImpl();
        this.invoiceDAO = new InvoiceDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    /**
     * Process order after successful payment
     * This method:
     * 1. Inserts order into Orders table
     * 2. Inserts delivery info
     * 3. Inserts order media items
     * 4. Reduces stock for each item
     * 5. Inserts payment transaction
     * 6. Inserts invoice
     * 7. Sends confirmation email
     * 
     * @param invoice           The invoice containing order and payment information
     * @param paymentMethod     Payment method used (e.g., "qr_code", "credit_card")
     * @param transactionNo     Transaction number from payment provider
     * @param bankCode          Bank code (for QR payments)
     * @param bankTransactionNo Bank transaction number (for QR payments)
     * @return true if successful, false otherwise
     */
    public boolean processCompletedOrder(Invoice invoice, String paymentMethod,
            String transactionNo, String bankCode,
            String bankTransactionNo) {
        if (invoice == null || invoice.getOrder() == null) {
            System.err.println("[OrderService] Invalid invoice or order");
            return false;
        }

        Order order = invoice.getOrder();
        List<CartItem> items = order.getItems();

        if (items == null || items.isEmpty()) {
            System.err.println("[OrderService] Order has no items");
            return false;
        }

        try {
            // Start transaction - all operations must succeed
            // Note: SQLite doesn't support explicit transactions well, but we'll handle
            // errors

            // 1. Insert Order using DAO
            long orderId = orderDAO.insertOrder(order);
            order.setOrderId(orderId);
            System.out.println("[OrderService] ✅ Inserted order: " + orderId);

            // 2. Insert DeliveryInfo using DAO
            if (order.getDeliveryInfo() != null) {
                order.getDeliveryInfo().setOrderId(orderId);
                deliveryInfoDAO.insertDeliveryInfo(order.getDeliveryInfo());
                System.out.println("[OrderService] ✅ Inserted delivery info for order: " + orderId);
            }

            // 3. Insert OrderMedia and reduce stock using DAO
            List<OrderMedia> orderMediaList = new ArrayList<>();
            for (CartItem item : items) {
                // Create OrderMedia entry
                OrderMedia orderMedia = new OrderMedia();
                orderMedia.setOrderId(orderId);
                orderMedia.setMediaId(item.getProduct().getId());
                orderMedia.setQuantity(item.getQuantity());
                orderMedia.setPrice(item.getProduct().getCurrentPrice());
                orderMediaList.add(orderMedia);

                // Reduce stock using ProductDAO
                boolean stockReduced = productDAO.reduceStock(item.getProduct().getId(), item.getQuantity());
                if (!stockReduced) {
                    System.err.println(
                            "[OrderService] ⚠️ Failed to reduce stock for product: " + item.getProduct().getId());
                    // Continue anyway - stock might have been reduced elsewhere
                } else {
                    System.out.println("[OrderService] ✅ Reduced stock for product: " + item.getProduct().getId()
                            + " by " + item.getQuantity());
                }
            }

            // Insert all OrderMedia items in batch using DAO
            orderMediaDAO.insertOrderMediaBatch(orderMediaList);
            System.out.println("[OrderService] ✅ Inserted " + orderMediaList.size() + " order media items");

            // 4. Insert PaymentTransaction using DAO
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setAmount(invoice.getTotalAmount());
            paymentTransaction.setMethodType(paymentMethod);
            paymentTransaction.setTransactionNo(transactionNo != null ? transactionNo : UUID.randomUUID().toString());
            paymentTransaction.setTransactionContent("Payment for order #" + orderId);
            paymentTransaction.setPayDate(new Timestamp(System.currentTimeMillis()));
            paymentTransaction.setBankCode(bankCode);
            paymentTransaction.setBankTransactionNo(bankTransactionNo);
            paymentTransaction.setCardType(paymentMethod.equals("credit_card") ? "PayPal" : null);

            long paymentTransactionId = paymentTransactionDAO.insertPaymentTransaction(paymentTransaction);
            paymentTransaction.setPaymentTransactionId(paymentTransactionId);
            System.out.println("[OrderService] ✅ Inserted payment transaction: " + paymentTransactionId);

            // 5. Insert Invoice using DAO
            invoice.setOrderId(orderId);
            invoice.setPaymentTransactionId(paymentTransactionId);
            long invoiceId = invoiceDAO.insertInvoice(invoice);
            invoice.setInvoiceId(invoiceId);
            System.out.println("[OrderService] ✅ Inserted invoice: " + invoiceId);

            // 6. Send confirmation email
            String customerEmail = null;
            if (order.getDeliveryInfo() != null) {
                customerEmail = order.getDeliveryInfo().getEmail();
            }

            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                // Send email in background thread to avoid blocking
                final String finalEmail = customerEmail;
                final Invoice finalInvoice = invoice;
                new Thread(() -> {
                    boolean emailSent = emailService.sendPaymentConfirmationEmail(finalInvoice, finalEmail);
                    if (emailSent) {
                        System.out.println("[OrderService] ✅ Payment confirmation email sent to: " + finalEmail);
                    } else {
                        System.err.println("[OrderService] ⚠️ Failed to send payment confirmation email");
                    }
                }).start();
            } else {
                System.out.println("[OrderService] ⚠️ No email address found, skipping email notification");
            }

            System.out.println("[OrderService] ✅ Order processing completed successfully for order: " + orderId);
            return true;

        } catch (SQLException e) {
            System.err.println("[OrderService] ❌ Error processing order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("[OrderService] ❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Process order for QR payment (VietQR)
     */
    public boolean processQRPaymentOrder(Invoice invoice, String transactionNo, String bankCode,
            String bankTransactionNo) {
        return processCompletedOrder(invoice, "qr_code", transactionNo, bankCode, bankTransactionNo);
    }

    /**
     * Process order for PayPal/Credit Card payment
     */
    public boolean processPayPalPaymentOrder(Invoice invoice, String paypalOrderId) {
        return processCompletedOrder(invoice, "credit_card", paypalOrderId, null, null);
    }
}
