package com.hust.soict.aims.subsystems.paypal;

public class PayPalConfig {

    // Client ID và Secret (Lấy từ tài khoản Developer PayPal của bạn)
    public static final String CLIENT_ID = "ASN_Jrw49xr4s31xuOmLJWzckyR39zXoYMBO1RyBF0nPvWGeVuNBbjBS9ddxMfX3rIHAMRD5WRdP80kJ";
    public static final String CLIENT_SECRET = "ENV0PdxhBKPBEAh7gAkBbvF__57LibT2sShkZefSPmoTctZxzhMJ4X8CVQbyXti2Bc_tcWGwYyNaAX_K";

    // API Base URL (Sandbox)
    public static final String API_BASE_URL = "https://api-m.sandbox.paypal.com";

    // [REFACTOR CLEAN CODE]: Tách các con số và chuỗi cứng ra đây
    public static final double VND_TO_USD_EXCHANGE_RATE = 24000.0;
    public static final String RETURN_URL = "http://localhost:8080/paypal-success";
    public static final String CANCEL_URL = "http://localhost:8080/paypal-cancel";
}
