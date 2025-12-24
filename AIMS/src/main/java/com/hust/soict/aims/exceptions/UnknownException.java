package com.hust.soict.aims.exceptions;

/**
 * Exception thrown for unknown payment errors
 */
public class UnknownException extends PaymentException {
    
    public UnknownException(String message) {
        super(message);
    }
    
    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}

