package com.example.mytree.exception;

public class UserDeleteForbiddenException extends RuntimeException {

	public UserDeleteForbiddenException(String requesterUserId, String targetUserId) {
		super("User " + requesterUserId + " cannot delete user: " + targetUserId);
	}
}
