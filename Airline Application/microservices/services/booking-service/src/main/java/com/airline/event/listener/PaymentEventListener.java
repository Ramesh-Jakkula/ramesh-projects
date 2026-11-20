package com.airline.event.listener;

import com.airline.client.FlightClient;
import com.airline.client.PricingClient;
import com.airline.client.UserClient;
import com.airline.enums.BookingStatus;
import com.airline.event.PaymentCompletedEvent;
import com.airline.event.PaymentFailedEvent;
import com.airline.event.publisher.BookingEventProducer;
import com.airline.model.Booking;
import com.airline.payload.dto.UserDTO;
import com.airline.payload.response.FareResponse;
import com.airline.payload.response.FlightInstanceResponse;
import com.airline.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.airline.services.TicketService;
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final BookingRepository bookingRepository;
    private final FlightClient flightClient;
    private final PricingClient pricingClient;
    private final UserClient userClient;
    private final BookingEventProducer bookingEventProducer;
    private final TicketService ticketService;

//    @KafkaListener(topics = "payment.completed", groupId = "booking-service-group")
//    @Transactional
//    public void handlePaymentCompleted(PaymentCompletedEvent event) throws Exception {
//
//        System.out.println("Received PaymentCompletedEvent "
//                + event.getBookingId()+"-"
//                + event.getPaymentId());
//
//        Booking booking = bookingRepository.findById(event.getBookingId())
//                .orElse(null);
//
//        if(booking == null) {return;}
//
//        booking.setStatus(BookingStatus.CONFIRMED);
//        bookingRepository.save(booking);
//
//        FlightInstanceResponse flightInstanceResponse=flightClient
//                .getFlightInstanceById(booking.getFlightInstanceId());
//        FareResponse fareResponse=pricingClient.getFareById(booking.getFareId());
//        UserDTO userDTO=userClient.getUserById(booking.getUserId());
//
//
//        bookingEventProducer.sendBookingConfirmed(booking,event,flightInstanceResponse,
//                fareResponse,userDTO);
//
//    }
@KafkaListener(
        topics = "payment.completed",
        groupId = "booking-service-group"
)
@Transactional
public void handlePaymentCompleted(PaymentCompletedEvent event)
        throws Exception {

    System.out.println("Received PaymentCompletedEvent "
            + event.getBookingId() + "-"
            + event.getPaymentId());

    Booking booking = bookingRepository.findById(event.getBookingId())
            .orElse(null);

    if (booking == null) {
        return;
    }

    // Step 1: Confirm booking after successful payment
    booking.setStatus(BookingStatus.CONFIRMED);

    // Step 2: Generate tickets only once
    // Generate tickets only once after successful payment
    if (!booking.isTicketIssued()) {

        if (booking.getPassengers() == null
                || booking.getPassengers().isEmpty()) {

            throw new IllegalStateException(
                    "No passengers found for booking: " + booking.getId()
            );
        }

        if (booking.getTickets() == null
                || booking.getTickets().isEmpty()) {

            ticketService.generateTicketsForBooking(booking);
        }

        if (booking.getTickets().size()
                != booking.getPassengers().size()) {

            throw new IllegalStateException(
                    "Ticket generation failed for booking: " + booking.getId()
            );
        }

        booking.setTicketIssued(true);
    }

    // Step 3: Save booking
    bookingRepository.save(booking);

    // Step 4: Retrieve required details
    FlightInstanceResponse flightInstanceResponse =
            flightClient.getFlightInstanceById(
                    booking.getFlightInstanceId()
            );

    FareResponse fareResponse =
            pricingClient.getFareById(booking.getFareId());

    UserDTO userDTO =
            userClient.getUserById(booking.getUserId());

    // Step 5: Publish booking confirmation event
    bookingEventProducer.sendBookingConfirmed(
            booking,
            event,
            flightInstanceResponse,
            fareResponse,
            userDTO
    );
}

    @KafkaListener(topics = "payment.failed",groupId = "booking-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) throws Exception {
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElse(null);

        if(booking == null) {return;}

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
