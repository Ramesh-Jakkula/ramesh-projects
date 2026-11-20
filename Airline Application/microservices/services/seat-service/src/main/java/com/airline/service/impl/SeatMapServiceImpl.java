package com.airline.service.impl;

import com.airline.client.AirlineClient;
import com.airline.mapper.SeatMapMapper;
import com.airline.model.CabinClass;
import com.airline.model.SeatMap;
import com.airline.payload.request.SeatMapRequest;
import com.airline.payload.response.AirlineResponse;
import com.airline.payload.response.SeatMapResponse;
import com.airline.repository.CabinClassRepository;
import com.airline.repository.SeatMapRepository;
import com.airline.service.SeatMapService;
import com.airline.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {

    private final CabinClassRepository cabinClassRepository;
    private final SeatMapRepository seatMapRepository;
    private final SeatService seatService;
    private final AirlineClient airlineClient;

    @Override
    public SeatMapResponse createSeatMap(Long userId, SeatMapRequest request) throws Exception {

        AirlineResponse airlineResponse=airlineClient.getAirlineByOwner(userId);
        CabinClass cabinClass=cabinClassRepository.findById(request.getCabinClassId())
                .orElseThrow(
                        ()->new Exception("cabin class not found with given id")
                );

        if(seatMapRepository.existsByAirlineIdAndCabinClassIdAndName(
                airlineResponse.getId(), request.getCabinClassId(), request.getName()
        )){
            throw new Exception("cabin class already exists with given name");
        }

        SeatMap seatMap= SeatMapMapper.toEntity(request, cabinClass);
        seatMap.setAirlineId(airlineResponse.getId());
        SeatMap savedSeatMap=seatMapRepository.save(seatMap);

        // generate seats for seat map
        seatService.generateSeats(savedSeatMap.getId());

        return SeatMapMapper.toResponse(savedSeatMap);
    }

    @Override
    public SeatMapResponse getSeatMapById(Long id) throws Exception {
        SeatMap seatMap=seatMapRepository.findById(id).orElseThrow(
                ()->new Exception("seat map not found with id")
        );
        return SeatMapMapper.toResponse(seatMap);
    }

    @Override
    public SeatMapResponse getSeatMapByCabinClass(Long cabinId) {
        SeatMap seatMap=seatMapRepository.findByCabinClassId(cabinId);

        return SeatMapMapper.toResponse(seatMap);
    }

    @Override
    public SeatMapResponse updateSeatMap(Long id, SeatMapRequest request) throws Exception {
        SeatMap seatMap=seatMapRepository.findById(id).orElseThrow(
                ()->new Exception("seat map not found with id")
        );
        SeatMapMapper.updateEntity(request, seatMap);
        SeatMap updated=seatMapRepository.save(seatMap);
        return SeatMapMapper.toResponse(updated);
    }

    @Override
    public void deleteSeatMap(Long id) throws Exception {
        SeatMap seatMap=seatMapRepository.findById(id).orElseThrow(
                ()->new Exception("seat map not found with id")
        );
        seatMapRepository.delete(seatMap);
    }
}
