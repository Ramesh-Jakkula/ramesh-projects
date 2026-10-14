package com.ram.dto;

import java.time.LocalDateTime;

public record CustomerAccountResponseDto(
        Long id,
        String customerName,
        String accountNumber,
        String accountType,
        Double balance,
        String branchCode,
        String branchName,
        String city,
        String phoneNumber,
        String email,
        String status,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {}