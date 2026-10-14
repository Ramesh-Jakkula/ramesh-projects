package com.ram.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ram.dto.CustomerAccountRequestDto;
import com.ram.dto.CustomerAccountResponseDto;
import com.ram.service.CustomerAccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class CustomerAccountController {

	private final CustomerAccountService service;

	// Create account
	@PostMapping
	public ResponseEntity<CustomerAccountResponseDto> create(@Valid @RequestBody CustomerAccountRequestDto request) {

		CustomerAccountResponseDto response = service.create(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// Get account by account number
	@GetMapping("/{accountNumber}")
	public ResponseEntity<CustomerAccountResponseDto> getByAccountNumber(@PathVariable String accountNumber) {

		return ResponseEntity.ok(service.getByAccountNumber(accountNumber));
	}

	// Get all accounts
	@GetMapping
	public ResponseEntity<List<CustomerAccountResponseDto>> getAll() {

		return ResponseEntity.ok(service.getAll());
	}

	// Update account by account number
	@PutMapping("/{accountNumber}")
	public ResponseEntity<CustomerAccountResponseDto> update(@PathVariable String accountNumber,
			@Valid @RequestBody CustomerAccountRequestDto request) {

		return ResponseEntity.ok(service.update(accountNumber, request));
	}

	// Delete account by account number
	@DeleteMapping("/{accountNumber}")
	public ResponseEntity<String> delete(@PathVariable String accountNumber) {

		return ResponseEntity.ok(service.delete(accountNumber));
	}
}
