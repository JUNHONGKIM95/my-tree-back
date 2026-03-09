package com.example.mytree.exception;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Invalid userId or password.");
	}
}
