package com.airline.service;

import com.airline.enums.SeatAvailabilityStatus;
import com.airline.payload.response.SeatInstanceResponse;

import java.util.List;

public interface SeatInstanceService {

    Double calculateSeatPrice(List<Long> seatInstanceIds);
    SeatInstanceResponse updateSeatInstanceStatus(Long seatInstanceId,
                                                  SeatAvailabilityStatus status);
}
