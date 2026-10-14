package com.ram.exception;

import com.ram.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// DTO validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				"Validation Failed", errors);

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(CustomerAccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCustomerAccountNotFoundException(CustomerAccountNotFoundException ex) {

		Map<String, String> errors = new HashMap<>();

		errors.put("accountNumber", ex.getMessage());

		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				"Customer account not found", errors);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

}
