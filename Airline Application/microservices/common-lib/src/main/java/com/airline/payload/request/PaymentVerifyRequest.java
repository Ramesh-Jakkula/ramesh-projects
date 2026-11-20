package com.airline.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerifyRequest {

    // Mock payment verification fields
    private Long paymentId;
    private Boolean success;
    private String failureReason;

    // Razorpay specific fields
   // private String razorpayPaymentId;
    // Stripe specific fields
   // private String stripePaymentIntentId;

}
