package com.hust.soict.aims.subsystems.vietqr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VietQRUrlBuilder {

    private final VietQRConfig cfg;

    public VietQRUrlBuilder(VietQRConfig cfg) {
        this.cfg = cfg;
    }

    public String build(long amount, String content) {
        String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(cfg.getAccountName(), StandardCharsets.UTF_8);

        return "https://img.vietqr.io/image/"
                + cfg.getBankId() + "-" + cfg.getAccountNo() + "-" + cfg.getTemplate() + ".png"
                + "?amount=" + amount
                + "&addInfo=" + encodedContent
                + "&accountName=" + encodedName;
    }
}
