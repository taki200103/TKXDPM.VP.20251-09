package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.enums.PaymentMethod;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.subsystems.IQRCodePayment;
import com.hust.soict.aims.subsystems.paypal.PayPalSubsystem;
import com.hust.soict.aims.subsystems.vietqr.VietQRSubsystem;

/**
 * Controller xử lý nghiệp vụ thanh toán đơn hàng
 * - Tạo payment (PayPal / VietQR)
 * - Xác nhận hoàn tất thanh toán
 */
public class PayOrderController {

    private final PlaceOrderController placeOrderController;

    // Subsystems
    private final IQRCodePayment vietQRSubsystem;
    private final PayPalSubsystem payPalSubsystem;

    public PayOrderController(PlaceOrderController placeOrderController) {
        this.placeOrderController = placeOrderController;
        this.vietQRSubsystem = new VietQRSubsystem();
        this.payPalSubsystem = new PayPalSubsystem();
    }

    /**
     * Tạo payment cho đơn hàng
     */
    public PaymentResult createPayment(Invoice invoice, PaymentMethod method)
            throws PaymentException {

        Order order = invoice.getOrder();
        int amount = (int) invoice.getTotal();
        String content = "AIMS_ORDER_" + order.getId();

        switch (method) {
            case VIETQR:
                return createVietQRPayment(amount, content);

            case PAYPAL:
                return createPayPalPayment(invoice);


            default:
                throw new PaymentException("Unsupported payment method: " + method);
        }
    }

    /**
     * Tạo thanh toán VietQR
     */
    private PaymentResult createVietQRPayment(int amount, String content)
            throws PaymentException {

        String qrUrl = vietQRSubsystem.generatePayUrl(amount, content);

        QRCode qrCode = new QRCode();
        qrCode.setQrCode(qrUrl);
        qrCode.setQrLink(qrUrl);
        qrCode.setBankCode("ICB");
        qrCode.setBankName("VietinBank");
        qrCode.setBankAccount("109875430178");

        return PaymentResult.vietQR(qrCode);
    }

    /**
     * Tạo thanh toán PayPal
     */
    private PaymentResult createPayPalPayment(Invoice invoice) throws PaymentException {
        String payUrl = payPalSubsystem.generatePayUrlForInvoice(invoice);
        return PaymentResult.paypal(payUrl);
    }

    /**
     * Hoàn tất đơn hàng sau khi thanh toán thành công
     */
    public PlaceOrderController.PlaceOrderResult completeOrder() {
        return placeOrderController.payOrder();
    }

    /**
     * Lấy order hiện tại
     */
    public Order getCurrentOrder() {
        return placeOrderController.getCurrentOrder();
    }
}
