package com.example.mytree.exception;

public class PostDeleteForbiddenException extends RuntimeException {

	public PostDeleteForbiddenException(Long postNo, String userId) {
		super("User " + userId + " cannot delete post: " + postNo);
	}
}
