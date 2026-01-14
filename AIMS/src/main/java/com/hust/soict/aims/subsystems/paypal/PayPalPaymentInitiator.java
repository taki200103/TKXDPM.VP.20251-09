package com.hust.soict.aims.subsystems.paypal;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.services.PaymentContextService;
import com.hust.soict.aims.subsystems.payment.PaymentInitiator;

public class PayPalPaymentInitiator implements PaymentInitiator {

    private final PayPalSubsystemController ctrl;
    private final PaymentContextService ctx;

    public PayPalPaymentInitiator(PayPalSubsystemController ctrl, PaymentContextService ctx) {
        this.ctrl = ctrl;
        this.ctx = ctx;
    }

    @Override
    public PaymentResult initiate(Invoice invoice) throws PaymentException {
        try {
            int amountVnd = (int) Math.round(invoice.getTotalAmount());
            PayPalOrderResponse res = ctrl.createOrder(amountVnd);

            // mapping orderId -> invoice để callback xử lý
            ctx.storePayPalOrder(res.getOrderId(), invoice);

            return PaymentResult.paypal(res.getApproveUrl());
        } catch (Exception e) {
            throw new PaymentException("PayPal error: " + e.getMessage(), e);
        }
    }
}
