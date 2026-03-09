package com.example.mytree.exception;

public class DuplicateUserIdException extends RuntimeException {

	public DuplicateUserIdException(String userId) {
		super("User already exists: " + userId);
	}
}
