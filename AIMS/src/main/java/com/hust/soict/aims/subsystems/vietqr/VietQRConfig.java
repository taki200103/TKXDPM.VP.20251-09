package com.hust.soict.aims.subsystems.vietqr;

public class VietQRConfig {
    private final String bankId;
    private final String bankName;
    private final String accountNo;
    private final String accountName;
    private final String template;

    public VietQRConfig(String bankId, String bankName, String accountNo, String accountName, String template) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.template = template;
    }

    public String getBankId() { return bankId; }
    public String getBankName() { return bankName; }
    public String getAccountNo() { return accountNo; }
    public String getAccountName() { return accountName; }
    public String getTemplate() { return template; }
}
