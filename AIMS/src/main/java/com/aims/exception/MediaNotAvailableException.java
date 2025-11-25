package com.aims.exception;

public class MediaNotAvailableException extends AimsException {

	private int quantityInCart;

	public MediaNotAvailableException() {
		this.quantityInCart = 0;
	}

	public MediaNotAvailableException(String message) {
		super(message);
	}
	public MediaNotAvailableException(int quantityInCart) {
		this.quantityInCart = quantityInCart;
	}
	public int getQuantityInCart() {
		return quantityInCart;
	}
}
