package com.airline.services.impl;

import com.airline.mapper.PassengerMapper;
import com.airline.model.Passenger;
import com.airline.payload.request.PassengerRequest;
import com.airline.repository.PassengerRepository;
import com.airline.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {


    private final PassengerRepository passengerRepository;

    @Override
    public Passenger createPassenger(PassengerRequest request, Long userId) {

        Passenger passenger = PassengerMapper.toEntity(request);
        passenger.setPrimaryUserId(userId);
        Passenger saved=passengerRepository.save(passenger);
        return saved;
    }


}
