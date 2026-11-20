package com.airline.services;

import com.airline.model.Booking;
import com.airline.model.Ticket;

import java.util.List;

public interface TicketService {

    List<Ticket> generateTicketsForBooking(Booking booking);
}
