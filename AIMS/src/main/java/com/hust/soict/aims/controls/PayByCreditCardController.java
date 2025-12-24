package com.hust.soict.aims.controls;

import com.fasterxml.jackson.databind.JsonNode;
import com.hust.soict.aims.boundaries.PayPalBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Desktop; 
import java.io.IOException;
import java.net.URI;    

@Component
public class PayByCreditCardController {
    @Autowired
    private PayPalBoundary payPalBoundary;

    public double convertVNDtoUSD(double amountVND) {
        return Math.ceil(amountVND / 25000.0 * 100) / 100;
    }

    public JsonNode startPayment(double amountVND) throws IOException, InterruptedException {
        double amountUSD = convertVNDtoUSD(amountVND);
        String returnUrl = "http://localhost:8080/paypal/return";
        String cancelUrl = "http://localhost:8080/paypal/cancel";
        return payPalBoundary.createOrder(amountUSD, returnUrl, cancelUrl);
    }

    public JsonNode captureOrder(String orderId) throws IOException, InterruptedException {
        return payPalBoundary.captureOrder(orderId);
    }

    public String getStatus(String orderId) { return payPalBoundary.getStatus(orderId); }

    public boolean executePaymentFlow(double totalVND) throws Exception {
        JsonNode node = startPayment(totalVND);
        
        String approve = null;
        for (JsonNode link : node.path("links")) {
            if ("approve".equals(link.path("rel").asText())) {
            	approve = link.path("href").asText();
            }
        }
        
        String orderId = node.path("id").asText();
        if (approve == null || approve.isEmpty()) {
            throw new IOException("Failed to create PayPal order (approve link missing).");
        }


        if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(approve));


        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 120_000) {
            String status = getStatus(orderId);
            if (status != null && status.contains("COMPLETED")) {
                return true; 
            }
            Thread.sleep(1000);
        }

        return false;
    }
}