package com.airline.service;

import com.airline.payload.request.FlightInstanceCabinRequest;
import com.airline.payload.response.FlightInstanceCabinResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightInstanceCabinService {

    FlightInstanceCabinResponse createFlightInstanceCabin(FlightInstanceCabinRequest request) throws Exception;
    FlightInstanceCabinResponse getFlightInstanceCabinById(Long id);
    Page<FlightInstanceCabinResponse> getByFlightInstanceId(Long id, Pageable pageable);
    FlightInstanceCabinResponse getByFlightInstanceIdAndCabinClassId(Long flightInstanceId, Long cabinClassId);
    FlightInstanceCabinResponse updateFlightInstanceCabin(Long id, FlightInstanceCabinRequest request);
    void deleteFlightInstanceCabin(Long id);
}
