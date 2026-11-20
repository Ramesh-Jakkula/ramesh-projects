package com.airline.service;

import com.airline.payload.dto.PaymentDTO;
import com.airline.payload.request.PaymentInitiateRequest;
import com.airline.payload.request.PaymentVerifyRequest;
import com.airline.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);

    PaymentDTO verifyPayment(PaymentVerifyRequest request);

    Page<PaymentDTO> getAllPayments(Pageable pageable);

    Map<Long, PaymentDTO> getPaymentsByBookingIds(List<Long> bookingIds);

}
