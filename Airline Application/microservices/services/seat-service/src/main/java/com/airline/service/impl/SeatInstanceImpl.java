package com.airline.service.impl;

import com.airline.enums.SeatAvailabilityStatus;
import com.airline.mapper.SeatInstanceMapper;
import com.airline.model.SeatInstance;
import com.airline.payload.response.SeatInstanceResponse;
import com.airline.repository.SeatInstanceRepository;
import com.airline.service.SeatInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatInstanceImpl implements SeatInstanceService {

    private final SeatInstanceRepository seatInstanceRepository;

    @Override
    public Double calculateSeatPrice(List<Long> seatInstanceIds) {
        List<SeatInstance> seatInstances=seatInstanceRepository.findAllById(seatInstanceIds);

        double price=0;
        for(SeatInstance si:seatInstances){
            double seatPremium=si.getPremiumSupercharge()!=null?
                    si.getPremiumSupercharge():0;
            price+=seatPremium;
        }
        return price;
    }

    @Override
    public SeatInstanceResponse updateSeatInstanceStatus(Long seatInstanceId,
                                                         SeatAvailabilityStatus status) {
        SeatInstance seatInstance=seatInstanceRepository.findById(seatInstanceId).orElse(null);
        if(seatInstance==null){return null;}
        seatInstance.setStatus(status);
        seatInstanceRepository.save(seatInstance);
        return SeatInstanceMapper.toResponse(seatInstance);
    }
}
