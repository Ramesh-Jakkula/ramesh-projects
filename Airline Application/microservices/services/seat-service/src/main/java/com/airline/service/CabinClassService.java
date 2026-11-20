package com.airline.service;

import com.airline.enums.CabinClassType;
import com.airline.payload.request.CabinClassRequest;
import com.airline.payload.response.CabinClassResponse;

import java.util.List;

public interface CabinClassService {

    CabinClassResponse createCabinClass(CabinClassRequest cabinClassRequest) throws Exception;
    CabinClassResponse getCabinClassById(Long id) throws Exception;
    List<CabinClassResponse> getCabinClassesByAircraftId(Long aircraftId);
    CabinClassResponse getByAircraftIdAndName(Long aircraftId, CabinClassType name);
    CabinClassResponse updateCabinClass(Long id, CabinClassRequest cabinClassRequest) throws Exception;
    void deleteCabinClass(Long id) throws Exception;



}
