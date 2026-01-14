package com.hust.soict.aims.subsystems.payment;

import com.hust.soict.aims.exceptions.PaymentException;

/**
 * Interface tách riêng cho refund (ISP)
 * Provider nào có refund thì implement, không ép tất cả phải có.
 */
public interface RefundablePayment {
    String refund(RefundRequest request) throws PaymentException;
}
