package com.aims.subsystem.paypal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

public class PayPalSubsystemController {

    private final HttpClient httpClient;

    public PayPalSubsystemController() {
        this.httpClient = HttpClient.newHttpClient();
    }

    // ... (Giữ nguyên hàm getAccessToken của bạn) ...
    private String getAccessToken() throws Exception {
        String auth = PayPalConfig.CLIENT_ID + ":" + PayPalConfig.CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PayPalConfig.API_BASE_URL + "/v1/oauth2/token"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("GET TOKEN FAILED: " + response.body());
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.get("access_token").getAsString();
    }

    /**
     * Refactor: Dùng GSON để tạo body JSON thay vì nối chuỗi
     */
    public String createOrder(int amount) throws Exception {
        String accessToken = getAccessToken();

        // Convert VND -> USD (Nên đưa tỷ giá vào Config)
        double amountUSD = amount / 24000.0;
        // Sử dụng Locale.US để đảm bảo luôn dùng dấu chấm động (.)
        String amountStr = String.format(Locale.US, "%.2f", amountUSD);

        // Xây dựng JSON Body bằng GSON
        JsonObject amountJson = new JsonObject();
        amountJson.addProperty("currency_code", "USD");
        amountJson.addProperty("value", amountStr);

        JsonObject purchaseUnit = new JsonObject();
        purchaseUnit.add("amount", amountJson);

        JsonArray purchaseUnits = new JsonArray();
        purchaseUnits.add(purchaseUnit);

        JsonObject orderRequest = new JsonObject();
        orderRequest.addProperty("intent", "CAPTURE");
        orderRequest.add("purchase_units", purchaseUnits);

        // Thêm return_url và cancel_url (PayPal yêu cầu cái này để redirect user sau khi thanh toán)
        JsonObject applicationContext = new JsonObject();
        applicationContext.addProperty("return_url", "http://localhost:8080/paypal-success"); // URL ảo demo
        applicationContext.addProperty("cancel_url", "http://localhost:8080/paypal-cancel");
        orderRequest.add("application_context", applicationContext);

        String requestBody = orderRequest.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PayPalConfig.API_BASE_URL + "/v2/checkout/orders"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            throw new Exception("CREATE ORDER FAILED: " + response.body());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

        // Cần lưu lại Order ID để lát nữa thực hiện Capture
        String orderId = jsonResponse.get("id").getAsString();
        System.out.println("PayPal Order ID: " + orderId);

        var links = jsonResponse.getAsJsonArray("links");
        for (var link : links) {
            JsonObject linkObj = link.getAsJsonObject();
            if ("approve".equals(linkObj.get("rel").getAsString())) {
                return linkObj.get("href").getAsString();
            }
        }

        throw new Exception("No approve link found");
    }

    /**
     * Bước 3: Capture Order (Thực hiện trừ tiền sau khi user approve)
     * Cần gọi hàm này khi user được redirect về return_url
     */
    public boolean captureOrder(String orderId) throws Exception {
        String accessToken = getAccessToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PayPalConfig.API_BASE_URL + "/v2/checkout/orders/" + orderId + "/capture"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody()) // Body rỗng
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            String status = jsonResponse.get("status").getAsString();
            return "COMPLETED".equals(status);
        }

        return false;
    }
}