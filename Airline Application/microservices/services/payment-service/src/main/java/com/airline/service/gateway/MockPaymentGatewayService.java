package com.airline.service.gateway;

import com.airline.model.Payment;
import com.airline.payload.response.PaymentLinkResponse;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentGatewayService {

    public PaymentLinkResponse createPaymentLink(Payment payment) {

        String mockOrderId = "MOCK_ORDER_" + payment.getId();

        String mockCheckoutReference =
                "MOCK_CHECKOUT_" + payment.getId();

        return PaymentLinkResponse.builder()
                .id(payment.getId())
                .payment_link_id(mockOrderId)
                .payment_link_url(mockCheckoutReference)
                .build();
    }

    public String generateMockPaymentId(Payment payment) {
        return "MOCK_PAYMENT_" + payment.getId();
    }
}

