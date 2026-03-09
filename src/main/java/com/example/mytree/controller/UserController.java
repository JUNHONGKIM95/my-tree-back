package com.example.mytree.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mytree.config.IpAddressResolver;
import com.example.mytree.dto.LoginRequest;
import com.example.mytree.dto.SignUpRequest;
import com.example.mytree.dto.UpdateUserRequest;
import com.example.mytree.dto.UserResponse;
import com.example.mytree.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final IpAddressResolver ipAddressResolver;

	public UserController(UserService userService, IpAddressResolver ipAddressResolver) {
		this.userService = userService;
		this.ipAddressResolver = ipAddressResolver;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(
		@Valid @RequestBody SignUpRequest request,
		HttpServletRequest httpServletRequest
	) {
		UserResponse response = userService.signUp(request, ipAddressResolver.resolve(httpServletRequest));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
		return ResponseEntity.ok(userService.getUser(userId));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getUsers() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUser(
		@PathVariable String userId,
		@Valid @RequestBody UpdateUserRequest request
	) {
		return ResponseEntity.ok(userService.updateUser(userId, request));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
}
