package com.aims.entity.payment;

import java.sql.Timestamp;

public class PaymentTransaction {
	private int id;
	private String transactionNo;
	private String bankCode;
	private String bankTransNo;
	private String cardType;
	private String methodType;
	private String transactionContent;
	private double amount;
	private Timestamp payDate;

	public PaymentTransaction(String transactionNo, String bankCode, String bankTransNo,
							  String cardType, String methodType, String transactionContent, double amount, Timestamp payDate) {
		this.transactionNo = transactionNo;
		this.bankCode = bankCode;
		this.bankTransNo = bankTransNo;
		this.cardType = cardType;
		this.methodType = methodType;
		this.transactionContent = transactionContent;
		this.amount = amount;
		this.payDate = payDate;
	}

	public PaymentTransaction() {

	}

	public int getId() {
		return id;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankTransNo() {
		return bankTransNo;
	}

	public void setBankTransNo(String bankTransNo) {
		this.bankTransNo = bankTransNo;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getTransactionContent() {
		return transactionContent;
	}

	public void setTransactionContent(String transactionContent) {
		this.transactionContent = transactionContent;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public Timestamp getPayDate() {
		return payDate;
	}

	public void setPayDate(Timestamp payDate) {
		this.payDate = payDate;
	}

	public void setId(int paymentTransactionId) {
		this.id = paymentTransactionId;
	}
}
