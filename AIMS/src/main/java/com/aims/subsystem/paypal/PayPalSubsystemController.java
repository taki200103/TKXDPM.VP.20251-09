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
     * Tạo đơn hàng trên PayPal (Create Order API)
     */
    public String createOrder(int amount) throws Exception {
        String accessToken = getAccessToken();

        // [CLEAN CODE]: Sử dụng tỷ giá từ Config thay vì hard-code số 24000
        double amountUSD = amount / PayPalConfig.VND_TO_USD_EXCHANGE_RATE;

        // Format số tiền (Luôn dùng dấu chấm thập phân chuẩn US)
        String amountStr = String.format(Locale.US, "%.2f", amountUSD);

        // Xây dựng JSON Body bằng thư viện GSON
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

        // [CLEAN CODE]: Sử dụng URL từ Config
        JsonObject applicationContext = new JsonObject();
        applicationContext.addProperty("return_url", PayPalConfig.RETURN_URL);
        applicationContext.addProperty("cancel_url", PayPalConfig.CANCEL_URL);

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

        // Lấy link approve để trả về cho người dùng
        var links = jsonResponse.getAsJsonArray("links");
        for (var link : links) {
            JsonObject linkObj = link.getAsJsonObject();
            if ("approve".equals(linkObj.get("rel").getAsString())) {
                return linkObj.get("href").getAsString();
            }
        }

        throw new Exception("No approve link found in PayPal response");
    }

    /**
     * Xác nhận thanh toán (Capture Order API)
     * Hàm này sẽ được gọi khi người dùng quay lại từ PayPal (logic mở rộng sau này)
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