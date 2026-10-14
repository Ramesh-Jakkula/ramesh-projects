package com.ram.exception;

public class CustomerAccountNotFoundException extends RuntimeException {

    public CustomerAccountNotFoundException(String accountNumber) {
        super("Customer account not found with account number: "
                + accountNumber);
    }
}