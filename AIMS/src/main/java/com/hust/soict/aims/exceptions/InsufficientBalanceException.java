package com.hust.soict.aims.exceptions;

/**
 * Exception thrown when payment account has insufficient balance
 */
public class InsufficientBalanceException extends PaymentException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

