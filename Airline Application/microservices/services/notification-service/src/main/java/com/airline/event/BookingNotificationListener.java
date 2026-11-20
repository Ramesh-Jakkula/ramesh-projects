package com.airline.event;

import com.airline.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class BookingNotificationListener {

    private final EmailService emailService;

    @KafkaListener(
            topics = "booking.confirmed",
            groupId = "notification-service-group"
    )
    @Transactional
    public void handleBookingConfirmed(
            @Payload BookingConfirmedEvent event
    ) throws MessagingException, UnsupportedEncodingException {

        System.out.println("event: -------------------- " + event);

        emailService.sendBookingConfirmation(event);

    }
}
