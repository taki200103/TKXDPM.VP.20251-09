package com.hust.soict.aims.subsystems.vietqr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Boundary layer for VietQR API communication
 * Handles HTTP requests to VietQR service
 */
public class VietQRBoundary {
    private static final String GET_TOKEN_URL = "https://dev.vietqr.org/vqr/api/token_generate";
    private static final String GENERATE_QR_URL = "https://dev.vietqr.org/vqr/api/qr/generate-customer";
    private static final String TEST_CALLBACK_URL = "https://dev.vietqr.org/vqr/bank/api/test/transaction-callback";
    
    private final HttpClient httpClient;
    
    public VietQRBoundary() {
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * Get access token from VietQR API
     * Uses Basic Authentication (username:password in Base64)
     * @param authorizationHeader Basic Auth header value (e.g., "Basic Base64String")
     * @return Response string containing access token
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    public String getAccessToken(String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GET_TOKEN_URL))
                .header("Authorization", authorizationHeader)
                .POST(HttpRequest.BodyPublishers.ofString(""))  // Empty body
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
    
    /**
     * Generate QR code
     * @param accessToken Bearer token from VietQR
     * @param requestString JSON request string
     * @return Response string containing QR code data
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    public String generateQRCode(String accessToken, String requestString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GENERATE_QR_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    /**
     * Check payment status (test callback)
     * @param accessToken Bearer token from VietQR
     * @param requestString JSON request string
     * @return Response string containing payment status
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    public String checkPaymentStatus(String accessToken, String requestString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEST_CALLBACK_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
}
