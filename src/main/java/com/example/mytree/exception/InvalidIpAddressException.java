package com.example.mytree.exception;

public class InvalidIpAddressException extends RuntimeException {

	public InvalidIpAddressException(String message) {
		super(message);
	}

	public InvalidIpAddressException(String message, Throwable cause) {
		super(message, cause);
	}
}
