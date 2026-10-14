package com.ram.dto;

import jakarta.validation.constraints.*;

public record CustomerAccountRequestDto(

        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,

        @NotBlank(message = "Account type is required")
        @Pattern(regexp = "SAVINGS|CURRENT",
                message = "Account type must be SAVINGS or CURRENT")
        String accountType,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance cannot be negative")
        Double balance,

        @NotBlank(message = "Branch code is required")
        @Size(max = 20, message = "Branch code must not exceed 20 characters")
        String branchCode,

        @NotBlank(message = "Branch name is required")
        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        String branchName,

        @NotBlank(message = "City is required")
        @Size(max = 60, message = "City must not exceed 60 characters")
        String city,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\d{10}",
                message = "Phone number must contain exactly 10 digits")
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 100)
        String email

) {}