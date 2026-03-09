package com.example.mytree.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mytree.domain.User;
import com.example.mytree.dto.LoginRequest;
import com.example.mytree.dto.SignUpRequest;
import com.example.mytree.dto.UpdateUserRequest;
import com.example.mytree.dto.UserResponse;
import com.example.mytree.exception.AdminUserDeletionNotAllowedException;
import com.example.mytree.exception.DuplicateUserIdException;
import com.example.mytree.exception.InvalidCredentialsException;
import com.example.mytree.exception.UserDeleteForbiddenException;
import com.example.mytree.exception.UserNotFoundException;
import com.example.mytree.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private static final String ADMIN_USER_ID = "admin";

	private final UserRepository userRepository;
	private final Clock clock;

	public UserService(UserRepository userRepository, Clock clock) {
		this.userRepository = userRepository;
		this.clock = clock;
	}

	@Transactional
	public UserResponse signUp(SignUpRequest request, String ipAddress) {
		if (userRepository.findByUserId(request.userId()) != null) {
			throw new DuplicateUserIdException(request.userId());
		}

		User user = new User(
			request.userId(),
			request.password(),
			request.name(),
			LocalDateTime.now(clock),
			ipAddress
		);
		userRepository.insert(user);
		return UserResponse.from(user);
	}

	public UserResponse login(LoginRequest request) {
		User user = userRepository.findByCredentials(request.userId(), request.password());
		if (user == null) {
			throw new InvalidCredentialsException();
		}
		return UserResponse.from(user);
	}

	public UserResponse getUser(String userId) {
		return UserResponse.from(getUserEntity(userId));
	}

	public List<UserResponse> getUsers() {
		return userRepository.findAll().stream().map(UserResponse::from).toList();
	}

	@Transactional
	public UserResponse updateUser(String userId, UpdateUserRequest request) {
		User existing = getUserEntity(userId);
		User user = new User(
			existing.getUserId(),
			request.password(),
			request.name(),
			existing.getCreatedAt(),
			existing.getIpAddress()
		);
		userRepository.update(user);
		return UserResponse.from(userRepository.findByUserId(userId));
	}

	@Transactional
	public void deleteUser(String userId, String requesterUserId) {
		if (requesterUserId == null || requesterUserId.isBlank()) {
			throw new UserNotFoundException("unknown");
		}

		if (userRepository.findByUserId(requesterUserId) == null) {
			throw new UserNotFoundException(requesterUserId);
		}

		if (!ADMIN_USER_ID.equals(requesterUserId)) {
			throw new UserDeleteForbiddenException(requesterUserId, userId);
		}

		if (ADMIN_USER_ID.equals(userId)) {
			throw new AdminUserDeletionNotAllowedException();
		}

		if (userRepository.deleteByUserId(userId) == 0) {
			throw new UserNotFoundException(userId);
		}
	}

	private User getUserEntity(String userId) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new UserNotFoundException(userId);
		}
		return user;
	}
}
