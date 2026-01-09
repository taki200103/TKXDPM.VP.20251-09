package com.hust.soict.aims.subsystems.paypal;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.services.PaymentContextService;
import com.hust.soict.aims.subsystems.IQRCodePayment;

public class PayPalSubsystem implements IQRCodePayment {

    private final PayPalSubsystemController ctrl;
    private final PaymentContextService paymentContextService = PaymentContextService.getInstance();

    public PayPalSubsystem() {
        this.ctrl = new PayPalSubsystemController();
    }

    // ✅ Flow đúng: user/invoice tạo PayPal order, lưu mapping, trả approve URL cho user
    public String generatePayUrlForInvoice(Invoice invoice) throws PaymentException {
        try {
            int amountVnd = (int) Math.round(invoice.getTotalAmount());

            PayPalOrderResponse res = ctrl.createOrder(amountVnd);

            // Lưu mapping để khi PayPal redirect về, ta biết invoice nào của user
            paymentContextService.storePayPalOrder(res.getOrderId(), invoice);

            return res.getApproveUrl();
        } catch (Exception e) {
            throw new PaymentException("Lỗi PayPal: " + e.getMessage(), e);
        }
    }

    @Override
    public String generatePayUrl(int amount, String content) throws PaymentException {
        throw new PaymentException("Không dùng generatePayUrl(amount, content) cho PayPal. Dùng generatePayUrlForInvoice(invoice).");
    }

    @Override
    public String refund(int amount, String content) throws PaymentException {
        return "Yêu cầu hoàn tiền PayPal thành công (Demo)";
    }
}
