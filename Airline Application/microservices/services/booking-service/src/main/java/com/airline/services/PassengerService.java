package com.airline.services;

import com.airline.model.Passenger;
import com.airline.payload.request.PassengerRequest;

public interface PassengerService {

    Passenger createPassenger(PassengerRequest request, Long userId);
}
