package com.hust.soict.aims.services;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.subsystems.paypal.PayPalSubsystemController;

public class PayPalCallbackService {

    private final PayPalStatusService statusService = PayPalStatusService.getInstance();
    private final PayPalSubsystemController paypalCtrl = new PayPalSubsystemController();
    private final PaymentContextService ctx = PaymentContextService.getInstance();
    private final OrderService orderService = new OrderService();

    public boolean handleSuccess(String orderId) {
        try {
            statusService.markPending(orderId);

            boolean ok = paypalCtrl.captureOrder(orderId);
            if (!ok) {
                statusService.markFailed(orderId);
                return false;
            }

            Invoice invoice = ctx.getInvoiceForPayPalOrder(orderId);
            if (invoice == null) {
                statusService.markFailed(orderId);
                return false;
            }

            boolean processed = orderService.processPayPalPaymentOrder(invoice, orderId);
            if (!processed) {
                statusService.markFailed(orderId);
                return false;
            }

            statusService.markCompleted(orderId);
            ctx.removePayPalOrder(orderId);
            return true;

        } catch (Exception e) {
            statusService.markFailed(orderId);
            e.printStackTrace();
            return false;
        }
    }

    public void handleCancel(String orderIdNullable) {
        // nếu muốn markCancelled theo orderId, bạn có thể truyền token/orderId vào cancel url
        // hiện tại bạn đang redirect thẳng.
    }
}
