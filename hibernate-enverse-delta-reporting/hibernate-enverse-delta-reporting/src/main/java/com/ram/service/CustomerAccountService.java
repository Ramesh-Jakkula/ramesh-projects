
package com.ram.service;

import com.ram.dto.CustomerAccountRequestDto;
import com.ram.dto.CustomerAccountResponseDto;
import java.util.List;

public interface CustomerAccountService {

	CustomerAccountResponseDto create(CustomerAccountRequestDto request);

	 CustomerAccountResponseDto getByAccountNumber(String accountNumber);

	List<CustomerAccountResponseDto> getAll();

    CustomerAccountResponseDto update(
            String accountNumber,
            CustomerAccountRequestDto request);

    String delete(String accountNumber);
}
