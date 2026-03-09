package com.example.mytree.dto;

import java.time.LocalDateTime;

import com.example.mytree.domain.Post;

public record PostResponse(
	Long postNo,
	String userId,
	String title,
	String content,
	LocalDateTime createdAt
) {
	public static PostResponse from(Post post) {
		return new PostResponse(
			post.getPostNo(),
			post.getUserId(),
			post.getTitle(),
			post.getContent(),
			post.getCreatedAt()
		);
	}
}
