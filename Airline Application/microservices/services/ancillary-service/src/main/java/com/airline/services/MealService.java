package com.airline.services;

import com.airline.payload.request.MealRequest;
import com.airline.payload.response.MealResponse;

import java.util.List;

public interface MealService {

    MealResponse createMeal(Long userId, MealRequest request) throws Exception;

    MealResponse getMealById(Long id) throws Exception;

    MealResponse updateMeal(Long userId, Long id, MealRequest request) throws Exception;

    List<MealResponse> getByAirlineId(Long userId);

    void deleteMeal(Long id) throws Exception;

    MealResponse updateAvailability(Long id, Boolean availability) throws Exception;

}
