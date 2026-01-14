package com.hust.soict.aims.subsystems.payment;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.exceptions.PaymentException;

/**
 * Strategy interface: khởi tạo thanh toán cho một invoice
 */
public interface PaymentInitiator {
    PaymentResult initiate(Invoice invoice) throws PaymentException;
}
