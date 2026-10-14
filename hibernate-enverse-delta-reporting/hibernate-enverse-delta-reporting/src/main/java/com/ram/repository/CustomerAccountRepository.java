package com.ram.repository;

import com.ram.model.CustomerAccount;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CustomerAccountRepository
        extends JpaRepository<CustomerAccount, Long> {

	boolean existsByAccountNumber(String accountNumber);
	Optional<CustomerAccount> findByAccountNumber(String accountNumber);
}