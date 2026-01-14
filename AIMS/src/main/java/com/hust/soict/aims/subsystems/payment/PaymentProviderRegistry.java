package com.hust.soict.aims.subsystems.payment;

import com.hust.soict.aims.entities.enums.PaymentMethod;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry/Factory:
 * PaymentMethod -> PaymentInitiator
 * giúp PayOrderController không switch-case (OCP)
 */
public class PaymentProviderRegistry {

    private final Map<PaymentMethod, PaymentInitiator> initiators = new EnumMap<>(PaymentMethod.class);

    public void register(PaymentMethod method, PaymentInitiator initiator) {
        if (method == null || initiator == null) return;
        initiators.put(method, initiator);
    }

    public PaymentInitiator getInitiator(PaymentMethod method) {
        return initiators.get(method);
    }
}
