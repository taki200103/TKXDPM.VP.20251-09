package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.entities.PaymentStatus;

/**
 * Controller for processing order payment
 * Coordinates between payment methods (QR, Credit Card) and order completion
 */
public class PayOrderController {
    private final IPaymentQRCode qrPaymentController;
    private final PlaceOrderController placeOrderController;
    
    /**
     * Create PayOrderController with payment controller and order controller
     * @param qrPaymentController QR payment implementation
     * @param placeOrderController Controller to complete order after payment
     */
    public PayOrderController(IPaymentQRCode qrPaymentController, PlaceOrderController placeOrderController) {
        this.qrPaymentController = qrPaymentController;
        this.placeOrderController = placeOrderController;
    }
    
    /**
     * Generate QR code for order payment
     * @param order Order to pay
     * @return QRCode containing payment information
     * @throws PaymentException if QR generation fails
     */
    public QRCode generatePaymentQR(Order order) throws PaymentException {
        return qrPaymentController.generateQRCode(order);
    }
    
    /**
     * Check if payment has been completed for order
     * @param order Order to check
     * @return PaymentStatus indicating payment state
     * @throws PaymentException if status check fails
     */
    public PaymentStatus checkPaymentStatus(Order order) throws PaymentException {
        return qrPaymentController.checkPaymentStatus(order);
    }
    
    /**
     * Complete order after payment confirmation
     * This reduces stock and finalizes the order
     * @return PlaceOrderResult with success status
     */
    public PlaceOrderController.PlaceOrderResult completeOrder() {
        return placeOrderController.payOrder();
    }
    
    /**
     * Get current order being processed
     * @return Order object
     */
    public Order getCurrentOrder() {
        return placeOrderController.getCurrentOrder();
    }
}
