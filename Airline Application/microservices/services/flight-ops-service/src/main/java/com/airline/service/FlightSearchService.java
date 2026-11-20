package com.airline.service;

import com.airline.payload.request.FlightSearchRequest;
import com.airline.payload.response.FlightInstanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightSearchService {

    Page<FlightInstanceResponse> searchFlights(FlightSearchRequest request, Pageable pageable);
}
