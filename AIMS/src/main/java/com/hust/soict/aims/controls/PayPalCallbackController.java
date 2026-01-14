package com.hust.soict.aims.controls;

import com.hust.soict.aims.services.PayPalCallbackService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayPalCallbackController {

    private final PayPalCallbackService callbackService = new PayPalCallbackService();

    @GetMapping("/paypal-success")
    public String paypalSuccess(@RequestParam("token") String orderId) {
        boolean ok = callbackService.handleSuccess(orderId);
        return ok ? "redirect:/payment-success" : "redirect:/payment-failed";
    }

    @GetMapping("/paypal-cancel")
    public String paypalCancel() {
        return "redirect:/payment-cancelled";
    }
}
