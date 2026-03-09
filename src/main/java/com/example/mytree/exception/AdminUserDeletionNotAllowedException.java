package com.example.mytree.exception;

public class AdminUserDeletionNotAllowedException extends RuntimeException {

	public AdminUserDeletionNotAllowedException() {
		super("The admin account cannot be deleted.");
	}
}
