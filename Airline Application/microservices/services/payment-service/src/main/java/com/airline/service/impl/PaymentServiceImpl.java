//package com.airline.service.impl;
//
//import com.razorpay.RazorpayException;
//import com.airline.client.UserClient;
//import com.airline.enums.PaymentGateway;
//import com.airline.enums.PaymentStatus;
//import com.airline.event.PaymentEventProducer;
//import com.airline.mapper.PaymentMapper;
//import com.airline.model.Payment;
//import com.airline.payload.dto.PaymentDTO;
//import com.airline.payload.dto.UserDTO;
//import com.airline.payload.request.PaymentInitiateRequest;
//import com.airline.payload.request.PaymentVerifyRequest;
//import com.airline.payload.response.PaymentInitiateResponse;
//import com.airline.payload.response.PaymentLinkResponse;
//import com.airline.repository.PaymentRepository;
//import com.airline.service.PaymentService;
//import com.airline.service.gateway.RazorpayService;
//import lombok.RequiredArgsConstructor;
//import org.json.JSONObject;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentServiceImpl implements PaymentService {
//
//
//    private final PaymentRepository paymentRepository;
//    private final RazorpayService razorpayService;
//    private final PaymentEventProducer paymentEventProducer;
//    private final UserClient userClient;
//
//    @Override
//    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws RazorpayException {
//        paymentRepository.findByBookingId(request.getBookingId())
//                .ifPresent(payment -> {
//                    if(payment.getStatus() == PaymentStatus.SUCCESS){
//                            throw new RuntimeException("payment already completed for this booking");
//                    }
//                });
//
//        Payment payment=Payment.builder()
//                .userId(request.getUserId())
//                .bookingId(request.getBookingId())
//                .amount(request.getAmount())
//                .provider(request.getGateway())
//                .status(PaymentStatus.PENDING)
//                .transactionId(generateTransactionId())
//                .build();
//
//        payment=paymentRepository.save(payment);
//
//        PaymentInitiateResponse response=PaymentInitiateResponse.builder()
//                .paymentId(payment.getId())
//                .gateway(request.getGateway())
//                .transactionId(payment.getTransactionId())
//                .amount(request.getAmount())
//                .description(request.getDescription())
//                .success(true)
//                .message("payment initiated successfully")
//                .build();
//
//        if(request.getGateway()== PaymentGateway.RAZORPAY){
////            fetch user details using feign client
//            UserDTO userDTO=userClient.getUserById(request.getUserId());
//
////          create razorpay payment link using razorpay service
//            PaymentLinkResponse paymentLinkResponse=razorpayService.createPaymentLink(
//                    userDTO, payment
//            );
//            // set payment link to payment initiate response
//            response.setRazorpayOrderId(paymentLinkResponse.getPayment_link_id());
//            response.setCheckoutUrl(paymentLinkResponse.getPayment_link_url());
//
//
//        }
//        return response;
//    }
//
//    private String generateTransactionId() {
//        return "TXN_"+System.currentTimeMillis()+"_"+
//                UUID.randomUUID().toString().substring(0,8).toUpperCase();
//    }
//
//    @Override
//    public PaymentDTO verifyPayment(PaymentVerifyRequest request) throws Exception {
//
//        JSONObject paymentDetails=razorpayService.fetchPaymentDetails(
//                request.getRazorpayPaymentId()
//        );
//
//        String status=paymentDetails.optString("status");
//
//        JSONObject notes=paymentDetails.getJSONObject("notes");
//        Long paymentId=Long.parseLong(notes.optString("payment_id"));
//
//        Payment payment=paymentRepository.findById(paymentId).orElseThrow(
//                () -> new Exception("payment not found")
//        );
//
//        boolean isValid="captured".equalsIgnoreCase(status);
//        if(isValid){
//            if(payment.getProvider()==PaymentGateway.RAZORPAY){
//                payment.setProviderPaymentId(request.getRazorpayPaymentId());
//            }
//            payment.setStatus(PaymentStatus.SUCCESS);
//            payment.setPaidAt(LocalDateTime.now());
//            paymentRepository.save(payment);
//
//
////          publish payment success kafka event
//
//            paymentEventProducer.sendPaymentCompleted(payment);
//
//        }else{
//            payment.setStatus(PaymentStatus.FAILED);
//            payment.setFailureReason("Payment verification failed");
//            paymentRepository.save(payment);
//
////          publish payment failed kafka event
//            paymentEventProducer.sendPaymentFailed(payment);
//        }
//        return PaymentMapper.toDTO(payment);
//    }
//
//    @Override
//    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
//        return paymentRepository.findAll(pageable).map(
//                PaymentMapper::toDTO
//        );
//    }
//
//    @Override
//    public Map<Long, PaymentDTO> getPaymentsByBookingIds(List<Long> bookingIds) {
//        return paymentRepository.findByBookingIdIn(bookingIds)
//                .stream().collect(Collectors.toMap(Payment::getId, PaymentMapper::toDTO));
//    }
//}

package com.airline.service.impl;

import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import com.airline.event.PaymentEventProducer;
import com.airline.mapper.PaymentMapper;
import com.airline.model.Payment;
import com.airline.payload.dto.PaymentDTO;
import com.airline.payload.request.PaymentInitiateRequest;
import com.airline.payload.request.PaymentVerifyRequest;
import com.airline.payload.response.PaymentInitiateResponse;
import com.airline.payload.response.PaymentLinkResponse;
import com.airline.repository.PaymentRepository;
import com.airline.service.PaymentService;
import com.airline.service.gateway.MockPaymentGatewayService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MockPaymentGatewayService mockPaymentGatewayService;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(
            PaymentInitiateRequest request) {

        paymentRepository.findByBookingId(request.getBookingId())
                .ifPresent(payment -> {
                    if (payment.getStatus() == PaymentStatus.SUCCESS) {
                        throw new IllegalStateException(
                                "Payment already completed for this booking"
                        );
                    }
                });

        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .provider(PaymentGateway.MOCK)
                .status(PaymentStatus.PENDING)
                .transactionId(generateTransactionId())
                .build();

        payment = paymentRepository.save(payment);

        PaymentLinkResponse mockResponse =
                mockPaymentGatewayService.createPaymentLink(payment);

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .gateway(payment.getProvider())
                .transactionId(payment.getTransactionId())
                .gatewayOrderId(mockResponse.getPayment_link_id())
                .amount(payment.getAmount())
                .description(request.getDescription())
                .checkoutUrl(mockResponse.getPayment_link_url())
                .success(true)
                .message("Mock payment initiated successfully")
                .build();
    }

    private String generateTransactionId() {
        return "TXN_"
                + System.currentTimeMillis()
                + "_"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    @Override
    @Transactional
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) {

        if (request.getPaymentId() == null) {
            throw new IllegalArgumentException(
                    "paymentId is required for mock verification"
            );
        }

        if (request.getSuccess() == null) {
            throw new IllegalArgumentException(
                    "success must be true or false"
            );
        }

        Payment payment = paymentRepository.findById(
                request.getPaymentId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Payment not found")
        );

        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.FAILED) {
            throw new IllegalStateException(
                    "Payment has already been verified"
            );
        }

        if (request.getSuccess()) {

            payment.setProviderPaymentId(
                    mockPaymentGatewayService.generateMockPaymentId(payment)
            );

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setFailureReason(null);

            payment = paymentRepository.save(payment);

            paymentEventProducer.sendPaymentCompleted(payment);

        } else {

            payment.setStatus(PaymentStatus.FAILED);

            payment.setFailureReason(
                    request.getFailureReason() != null
                            && !request.getFailureReason().isBlank()
                            ? request.getFailureReason()
                            : "Mock payment failed"
            );

            payment = paymentRepository.save(payment);

            paymentEventProducer.sendPaymentFailed(payment);
        }

        return PaymentMapper.toDTO(payment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {

        return paymentRepository.findAll(pageable)
                .map(PaymentMapper::toDTO);
    }

    @Override
    public Map<Long, PaymentDTO> getPaymentsByBookingIds(
            List<Long> bookingIds) {

        return paymentRepository.findByBookingIdIn(bookingIds)
                .stream()
                .collect(Collectors.toMap(
                        Payment::getBookingId,
                        PaymentMapper::toDTO,
                        (existing, replacement) -> replacement
                ));
    }
}
