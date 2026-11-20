package com.airline.client;

import com.airline.payload.response.FlightInstanceResponse;
import com.airline.payload.response.FlightResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("flight-ops-service")
public interface FlightClient {

    @GetMapping("/api/flights/{id}")
    FlightResponse getFlightById(@PathVariable Long id);

    @GetMapping("/api/flight-instances/{id}")
    FlightInstanceResponse getFlightInstanceById(
            @PathVariable Long id);
}
