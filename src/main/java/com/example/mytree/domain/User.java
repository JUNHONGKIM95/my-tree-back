package com.example.mytree.domain;

import java.time.LocalDateTime;

public class User {

	private String userId;
	private String password;
	private String name;
	private LocalDateTime createdAt;
	private String ipAddress;

	public User() {
	}

	public User(String userId, String password, String name, LocalDateTime createdAt, String ipAddress) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.createdAt = createdAt;
		this.ipAddress = ipAddress;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
