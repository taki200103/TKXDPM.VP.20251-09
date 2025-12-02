package com.aims.exception;

public class UnrecognizedException extends RuntimeException {
	public UnrecognizedException() {
		super("ERROR: Something went wrowng!");
	}
}
