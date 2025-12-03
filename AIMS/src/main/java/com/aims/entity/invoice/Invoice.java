package com.aims.entity.invoice;

import com.aims.entity.order.Order;
import com.aims.entity.payment.PaymentTransaction;

public class Invoice {
    private int id;
    private Order order;
    private int amount;
    private PaymentTransaction paymentTransaction;

    public Invoice(Order order){
        this.order = order;
        this.amount = order.getAmount() + order.getShippingFees();
    }

    public Order getOrder() {
        return order;
    }

    public void setAmount() {
        this.amount = order.getAmount() + order.getShippingFees();
    }

    public int getAmount() {
        setAmount();
        return amount;
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
