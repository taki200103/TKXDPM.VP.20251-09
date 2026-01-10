package com.hust.soict.aims.controls;

import com.hust.soict.aims.services.PayPalStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayPalStatusController {

    private final PayPalStatusService statusService = PayPalStatusService.getInstance();

    @GetMapping("/paypal-status")
    public String paypalStatus(@RequestParam("orderId") String orderId) {
        return statusService.getStatus(orderId).name();
    }
}
