package com.example.mytree.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.mytree.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateUserIdException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateUserId(DuplicateUserIdException ex) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(PostNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(PostDeleteForbiddenException.class)
	public ResponseEntity<ErrorResponse> handlePostDeleteForbidden(PostDeleteForbiddenException ex) {
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(UserDeleteForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleUserDeleteForbidden(UserDeleteForbiddenException ex) {
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(AdminUserDeletionNotAllowedException.class)
	public ResponseEntity<ErrorResponse> handleAdminUserDeletionNotAllowed(AdminUserDeletionNotAllowedException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(InvalidIpAddressException.class)
	public ResponseEntity<ErrorResponse> handleInvalidIpAddress(InvalidIpAddressException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
		String message = fieldError == null ? "Validation failed." : fieldError.getDefaultMessage();
		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
		ErrorResponse body = new ErrorResponse(
			LocalDateTime.now(),
			status.value(),
			status.getReasonPhrase(),
			message
		);
		return ResponseEntity.status(status).body(body);
	}
}
