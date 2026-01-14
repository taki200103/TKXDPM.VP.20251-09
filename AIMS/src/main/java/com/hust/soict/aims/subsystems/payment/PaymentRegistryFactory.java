package com.hust.soict.aims.subsystems.payment;

import com.hust.soict.aims.entities.enums.PaymentMethod;
import com.hust.soict.aims.services.PaymentContextService;
import com.hust.soict.aims.subsystems.paypal.PayPalPaymentInitiator;
import com.hust.soict.aims.subsystems.paypal.PayPalSubsystemController;
import com.hust.soict.aims.subsystems.vietqr.VietQRConfig;
import com.hust.soict.aims.subsystems.vietqr.VietQRPaymentInitiator;

public class PaymentRegistryFactory {

    private PaymentRegistryFactory() {}

    public static PaymentProviderRegistry createDefaultRegistry() {
        PaymentProviderRegistry registry = new PaymentProviderRegistry();

        // VietQR config (có thể chuyển ra file config sau)
        VietQRConfig vietQRConfig = new VietQRConfig(
                "ICB",
                "VietinBank",
                "109875430178",
                "PHAM MINH DAT",
                "compact"
        );
        registry.register(PaymentMethod.VIETQR, new VietQRPaymentInitiator(vietQRConfig));

        // PayPal
        registry.register(
                PaymentMethod.PAYPAL,
                new PayPalPaymentInitiator(
                        new PayPalSubsystemController(),
                        PaymentContextService.getInstance()
                )
        );

        return registry;
    }
}
