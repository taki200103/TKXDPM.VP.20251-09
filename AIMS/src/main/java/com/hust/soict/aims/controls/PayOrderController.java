package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.entities.enums.PaymentMethod;
import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.subsystems.payment.PaymentInitiator;
import com.hust.soict.aims.subsystems.payment.PaymentProviderRegistry;

/**
 * Controller xử lý nghiệp vụ thanh toán đơn hàng
 * - Tạo payment (PayPal / VietQR)
 * - Xác nhận hoàn tất thanh toán
 *
 * SOLID:
 * - OCP: thêm phương thức mới không sửa logic (chỉ register strategy)
 * - DIP: phụ thuộc abstraction (registry + initiator)
 */
public class PayOrderController {

    private final PlaceOrderController placeOrderController;
    private final PaymentProviderRegistry registry;

    public PayOrderController(PlaceOrderController placeOrderController, PaymentProviderRegistry registry) {
        this.placeOrderController = placeOrderController;
        this.registry = registry;
    }

    /**
     * Tạo payment cho đơn hàng
     */
    public PaymentResult createPayment(Invoice invoice, PaymentMethod method) throws PaymentException {
        PaymentInitiator initiator = registry.getInitiator(method);
        if (initiator == null) {
            throw new PaymentException("Unsupported payment method: " + method);
        }
        return initiator.initiate(invoice);
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
