package com.hust.soict.aims.boundaries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class PayPalBoundary {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${paypal.clientId:}")
    private String clientId;
    @Value("${paypal.clientSecret:}")
    private String clientSecret;
    @Value("${paypal.mode:sandbox}")
    private String mode;

    // order status store
    private final Map<String, String> orderStatus = new ConcurrentHashMap<>();

    private String paypalApiBase() {
        if ("live".equalsIgnoreCase(mode)) return "https://api-m.paypal.com";
        return "https://api-m.sandbox.paypal.com";
    }

    // internal method - can be called by controller bean directly
    public JsonNode createOrder(double amount, String returnUrl, String cancelUrl) throws IOException, InterruptedException {
        String token = getAccessToken();
        String url = paypalApiBase() + "/v2/checkout/orders";
        String body = "{\"intent\":\"CAPTURE\",\"purchase_units\":[{\"amount\":{\"currency_code\":\"USD\",\"value\":\"" + String.format("%.2f", amount) + "\"}}],\"application_context\":{\"return_url\":\"" + returnUrl + "\",\"cancel_url\":\"" + cancelUrl + "\"}}";

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode node = mapper.readTree(resp.body());
        String id = node.path("id").asText();
        orderStatus.put(id, "CREATED");
        return node;
    }

    // internal method to capture
    public JsonNode captureOrder(String orderId) throws IOException, InterruptedException {
        String token = getAccessToken();
        String url = paypalApiBase() + "/v2/checkout/orders/" + orderId + "/capture";
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode node = mapper.readTree(resp.body());
        orderStatus.put(orderId, "COMPLETED");
        return node;
    }

    public String getStatus(String orderId) { return orderStatus.getOrDefault(orderId, "UNKNOWN"); }

    private String getAccessToken() throws IOException, InterruptedException {
        String url = paypalApiBase() + "/v1/oauth2/token";
        String creds = clientId + ":" + clientSecret;
        String basic = Base64.getEncoder().encodeToString(creds.getBytes());
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode node = mapper.readTree(resp.body());
        return node.path("access_token").asText();
    }

    // REST endpoints for external callers (keep create-order and status)
    @PostMapping(path = "/paypal/create-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createOrderEndpoint(@RequestParam double amount, @RequestParam String returnUrl, @RequestParam String cancelUrl) throws IOException, InterruptedException {
        return createOrder(amount, returnUrl, cancelUrl);
    }

    @GetMapping(path = "/paypal/return")
    public String paypalReturn(@RequestParam(name = "token", required = false) String token) throws IOException, InterruptedException {
        if (token != null && !token.isEmpty()) {
            captureOrder(token);
            return "<html><body><h3>Payment processed. You can close this window and return to the application.</h3></body></html>";
        }
        return "<html><body><h3>Missing token</h3></body></html>";
    }

    @GetMapping(path = "/paypal/status")
    public String status(@RequestParam(name = "orderId") String orderId) {
        return getStatus(orderId);
    }
}
