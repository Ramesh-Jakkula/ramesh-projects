package com.airline.controller;

import com.airline.payload.request.CityRequest;
import com.airline.payload.response.ApiResponse;
import com.airline.payload.response.CityResponse;
import com.airline.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

//    /api/cities
    @PostMapping
    public ResponseEntity<CityResponse> createCity(
            @Valid @RequestBody CityRequest cityRequest
    ) throws Exception {
        CityResponse res=cityService.createCity(cityRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

//    /api/cities/3
    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCityById(
            @PathVariable Long id
    ) throws Exception {
        CityResponse res=cityService.getCityById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<Page<CityResponse>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(cityService.getAllCities(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> updateCity(
            @PathVariable Long id,
            @Valid @RequestBody CityRequest request)
            throws Exception {
        return ResponseEntity.ok(cityService.updateCity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCity(@PathVariable Long id)
            throws Exception {
        cityService.deleteCity(id);
        return ResponseEntity.ok(new ApiResponse("City deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CityResponse>> searchCities(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cityService.searchCities(keyword, pageable));
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<Page<CityResponse>> getCitiesByCountryCode(
            @PathVariable String countryCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cityService.getCitiesByCountryCode(countryCode.toUpperCase(), pageable));
    }

    @GetMapping("/exists/{cityCode}")
    public ResponseEntity<Boolean> checkCityExists(@PathVariable String cityCode) {
        return ResponseEntity.ok(cityService.cityExists(cityCode.toUpperCase()));
    }

}
