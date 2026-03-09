package com.example.mytree.exception;

public class PostNotFoundException extends RuntimeException {

	public PostNotFoundException(Long postNo) {
		super("Post not found: " + postNo);
	}
}
