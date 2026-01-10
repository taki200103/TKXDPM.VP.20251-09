package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.services.EmailService;
import com.hust.soict.aims.services.OrderService;
import com.hust.soict.aims.services.PaymentContextService;
import com.hust.soict.aims.subsystems.paypal.PayPalSubsystemController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.hust.soict.aims.services.PayPalStatusService;

@Controller
public class PayPalCallbackController {
    private final PayPalStatusService statusService = PayPalStatusService.getInstance();
    private final PayPalSubsystemController paypalCtrl = new PayPalSubsystemController();
    private final PaymentContextService ctx = PaymentContextService.getInstance();
    private final EmailService emailService = EmailService.getInstance();
    private final OrderService orderService = new OrderService();

    @GetMapping("/paypal-success")
    public String paypalSuccess(@RequestParam("token") String orderId) {
        try {
            statusService.markPending(orderId);

            boolean ok = paypalCtrl.captureOrder(orderId);
            if (!ok) {
                statusService.markFailed(orderId);
                return "redirect:/payment-failed";
            }

            Invoice invoice = ctx.getInvoiceForPayPalOrder(orderId);
            if (invoice == null) {
                statusService.markFailed(orderId);
                return "redirect:/payment-failed";
            }

            boolean processed = orderService.processPayPalPaymentOrder(invoice, orderId);
            if (!processed) {
                statusService.markFailed(orderId);
                return "redirect:/payment-failed";
            }

            statusService.markCompleted(orderId);

            ctx.removePayPalOrder(orderId);
            return "redirect:/payment-success";

        } catch (Exception e) {
            statusService.markFailed(orderId);
            e.printStackTrace();
            return "redirect:/payment-failed";
        }
    }


    @GetMapping("/paypal-cancel")
    public String paypalCancel() {
        return "redirect:/payment-cancelled";
    }
}
