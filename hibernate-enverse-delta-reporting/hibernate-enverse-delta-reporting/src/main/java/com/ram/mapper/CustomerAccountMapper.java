package com.ram.mapper;

import com.ram.dto.CustomerAccountRequestDto;
import com.ram.dto.CustomerAccountResponseDto;
import com.ram.enums.AccountStatus;
import com.ram.model.CustomerAccount;
import org.springframework.stereotype.Component;

@Component
public class CustomerAccountMapper {

    public CustomerAccount toEntity(CustomerAccountRequestDto dto) {
        return CustomerAccount.builder()
                .customerName(dto.customerName())
                .accountType(dto.accountType())
                .balance(dto.balance())
                .branchCode(dto.branchCode())
                .branchName(dto.branchName())
                .city(dto.city())
                .phoneNumber(dto.phoneNumber())
                .email(dto.email())
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public void updateEntity(
            CustomerAccount entity,
            CustomerAccountRequestDto dto) {

        entity.setCustomerName(dto.customerName());
        entity.setAccountType(dto.accountType());
        entity.setBalance(dto.balance());
        entity.setBranchCode(dto.branchCode());
        entity.setBranchName(dto.branchName());
        entity.setCity(dto.city());
        entity.setPhoneNumber(dto.phoneNumber());
        entity.setEmail(dto.email());

        // Account number, status and audit timestamps are not changed here.
    }

    public CustomerAccountResponseDto toResponse(
            CustomerAccount entity) {

        return new CustomerAccountResponseDto(
                entity.getId(),
                entity.getCustomerName(),
                entity.getAccountNumber(),
                entity.getAccountType(),
                entity.getBalance(),
                entity.getBranchCode(),
                entity.getBranchName(),
                entity.getCity(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getLastModifiedAt()
        );
    }
}