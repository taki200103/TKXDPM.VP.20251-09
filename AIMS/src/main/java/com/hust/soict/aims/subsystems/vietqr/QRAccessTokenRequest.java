package com.hust.soict.aims.subsystems.vietqr;

import java.util.Base64;

/**
 * Request for getting VietQR access token
 * Uses Basic Authentication (username:password encoded in Base64)
 */
public class QRAccessTokenRequest {
    private String username;
    private String password;
    
    public QRAccessTokenRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    
    public String buildAuthorizationHeader() {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

