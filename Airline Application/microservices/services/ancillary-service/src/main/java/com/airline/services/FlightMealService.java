package com.airline.services;

import com.airline.payload.request.FlightMealRequest;
import com.airline.payload.response.FlightMealResponse;

import java.util.List;

public interface FlightMealService {

    FlightMealResponse createFlightMeal(FlightMealRequest request) throws Exception;
    FlightMealResponse getFlightMealById(Long id) throws Exception;
    List<FlightMealResponse> getByFlightId(Long flightId);
    List<FlightMealResponse> getAllByIds(List<Long> ids);
    FlightMealResponse updateFlightMeal(Long id, FlightMealRequest request) throws Exception;
    void deleteFlightMeal(Long id) throws Exception;
    FlightMealResponse updateFlightMealAvailability(Long id, Boolean availability) throws Exception;
    Double calculateMealPrice(List<Long> mealIds);
}
