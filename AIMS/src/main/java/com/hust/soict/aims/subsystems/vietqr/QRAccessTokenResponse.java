package com.hust.soict.aims.subsystems.vietqr;

/**
 * Response containing access token from VietQR
 * Response format:
 * {
 *   "access_token": "eyJhbGciOiJIUzUxMiJ9...",
 *   "token_type": "Bearer",
 *   "expires_in": 300
 * }
 */
public class QRAccessTokenResponse {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    
    public QRAccessTokenResponse() {}
    
    /**
     * Parse response string from VietQR API
     * @param responseString JSON response string from VietQR
     */
    public void parseResponseString(String responseString) {
        if (responseString == null || responseString.isEmpty()) {
            return;
        }
        
        // Simple JSON parsing (in production, use Jackson or Gson)
        // Extract access_token
        if (responseString.contains("access_token")) {
            int start = responseString.indexOf("\"access_token\":\"") + 16;
            int end = responseString.indexOf("\"", start);
            if (start > 15 && end > start) {
                this.accessToken = responseString.substring(start, end);
            }
        }
        
        // Extract token_type
        if (responseString.contains("token_type")) {
            int start = responseString.indexOf("\"token_type\":\"") + 14;
            int end = responseString.indexOf("\"", start);
            if (start > 13 && end > start) {
                this.tokenType = responseString.substring(start, end);
            }
        }
        
        // Extract expires_in
        if (responseString.contains("expires_in")) {
            int start = responseString.indexOf("\"expires_in\":") + 13;
            int end = start;
            // Find end of number
            while (end < responseString.length() && 
                   (Character.isDigit(responseString.charAt(end)) || responseString.charAt(end) == ' ')) {
                end++;
            }
            if (start < end) {
                try {
                    this.expiresIn = Integer.parseInt(responseString.substring(start, end).trim());
                } catch (NumberFormatException e) {
                    this.expiresIn = 300; // Default 5 minutes
                }
            }
        }
    }
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    
    /**
     * Check if response is valid
     */
    public boolean isValid() {
        return accessToken != null && !accessToken.isEmpty();
    }
}
