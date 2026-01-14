package com.hust.soict.aims.subsystems.vietqr;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.subsystems.payment.PaymentInitiator;

public class VietQRPaymentInitiator implements PaymentInitiator {

    private final VietQRConfig cfg;
    private final VietQRUrlBuilder builder;

    public VietQRPaymentInitiator(VietQRConfig cfg) {
        this.cfg = cfg;
        this.builder = new VietQRUrlBuilder(cfg);
    }

    @Override
    public PaymentResult initiate(Invoice invoice) throws PaymentException {
        try {
            long amount = Math.round(invoice.getTotalAmount());
            Order order = invoice.getOrder();
            String content = "AIMS_ORDER_" + (order != null ? order.getOrderId() : "UNKNOWN");

            String qrUrl = builder.build(amount, content);

            QRCode qr = new QRCode();
            qr.setQrCode(qrUrl);
            qr.setQrLink(qrUrl);
            qr.setBankCode(cfg.getBankId());
            qr.setBankName(cfg.getBankName());
            qr.setBankAccount(cfg.getAccountNo());

            return PaymentResult.vietQR(qr);
        } catch (Exception e) {
            throw new PaymentException("VietQR error: " + e.getMessage(), e);
        }
    }
}
