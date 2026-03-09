package com.example.mytree.dto;

import java.time.LocalDateTime;

import com.example.mytree.domain.User;

public record UserResponse(
	String userId,
	String name,
	LocalDateTime createdAt,
	String ipAddress
) {
	public static UserResponse from(User user) {
		return new UserResponse(
			user.getUserId(),
			user.getName(),
			user.getCreatedAt(),
			user.getIpAddress()
		);
	}
}
