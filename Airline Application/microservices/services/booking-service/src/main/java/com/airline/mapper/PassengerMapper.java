package com.airline.mapper;

import com.airline.model.Passenger;
import com.airline.payload.request.PassengerRequest;
import com.airline.payload.response.PassengerResponse;

public class PassengerMapper {

    public static Passenger toEntity(PassengerRequest request){
        return Passenger.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())

                .nationality(request.getNationality())
                .build();
    }


    public static PassengerResponse toResponse(Passenger passenger) {
        return PassengerResponse.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .email(passenger.getEmail())
                .phone(passenger.getPhone())
                .dateOfBirth(passenger.getDateOfBirth())
                .gender(passenger.getGender())

                .nationality(passenger.getNationality())

                .primaryUserId(passenger.getPrimaryUserId())

                .isActive(passenger.getIsActive())
                .age(passenger.getAge())
                .isAdult(passenger.isAdult())
                .fullName(passenger.getFullName())
                .createdAt(passenger.getCreatedAt())
                .updatedAt(passenger.getUpdatedAt())
                .build();
    }
}
