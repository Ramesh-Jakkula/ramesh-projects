package com.ram.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ram.dto.CustomerAccountRequestDto;
import com.ram.dto.CustomerAccountResponseDto;
import com.ram.enums.AccountStatus;
import com.ram.exception.CustomerAccountNotFoundException;
import com.ram.mapper.CustomerAccountMapper;
import com.ram.model.CustomerAccount;
import com.ram.repository.CustomerAccountRepository;
import com.ram.service.CustomerAccountService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerAccountServiceImpl implements CustomerAccountService {

	private final CustomerAccountRepository repository;
	private final CustomerAccountMapper mapper;

	@Override
	public CustomerAccountResponseDto create(CustomerAccountRequestDto request) {

		CustomerAccount account = mapper.toEntity(request);

		account.setAccountNumber(generateAccountNumber());
		account.setStatus(AccountStatus.ACTIVE);

		CustomerAccount savedAccount = repository.save(account);

		return mapper.toResponse(savedAccount);
	}

	private String generateAccountNumber() {
		return "ACC" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	@Override
	public CustomerAccountResponseDto getByAccountNumber(String accountNumber) {
		CustomerAccount existingAccount = repository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new CustomerAccountNotFoundException(accountNumber));

		return mapper.toResponse(existingAccount);
	}

	@Override
	public List<CustomerAccountResponseDto> getAll() {
		return repository.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	public CustomerAccountResponseDto update(String accountNumber, CustomerAccountRequestDto request) {
		CustomerAccount existingAccount = repository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new CustomerAccountNotFoundException(accountNumber));

		mapper.updateEntity(existingAccount, request);

		CustomerAccount updatedAccount = repository.save(existingAccount);

		return mapper.toResponse(updatedAccount);
	}

	@Override
	public String delete(String accountNumber) {

		CustomerAccount existingAccount = repository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new CustomerAccountNotFoundException(accountNumber));

		repository.delete(existingAccount);

		return "Account deleted successfully with account number: " + accountNumber;
	}

}
