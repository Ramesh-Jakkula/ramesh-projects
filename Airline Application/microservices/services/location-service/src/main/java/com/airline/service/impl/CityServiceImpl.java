package com.airline.service.impl;

import com.airline.mapper.CityMapper;
import com.airline.model.City;
import com.airline.payload.request.CityRequest;
import com.airline.payload.response.CityResponse;
import com.airline.repository.CityRepository;
import com.airline.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {


    private final CityRepository cityRepository;

    @Override
    public CityResponse createCity(CityRequest request) throws Exception {

        if(cityRepository.existsByCityCode(request.getCityCode())){
            throw new Exception("city with given code already exist");
        }

        City city= CityMapper.toEntity(request);
        City result = cityRepository.save(city);
        return CityMapper.toResponse(result);
    }

    @Override
    @Cacheable(cacheNames = "cities", key = "#id")
    public CityResponse getCityById(Long id) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(
                ()-> new Exception("city not exit with given id")
        );
        return CityMapper.toResponse(city);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "cities", key = "#id"),
            @CacheEvict(cacheNames = "citiesByCode", allEntries = true)
    })
    public CityResponse updateCity(Long id, CityRequest request) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(
                ()-> new Exception("city not exit with given id")
        );

        if(cityRepository.existsByCityCodeAndIdNot(request.getCityCode(),id)){
            throw new Exception("city with given code already exist");
        }

        City updatedCity=cityRepository.save(CityMapper.updateEntity(city,request));
        return CityMapper.toResponse(updatedCity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "cities", key = "#id"),
            @CacheEvict(cacheNames = "citiesByCode", allEntries = true)
    })
    public void deleteCity(Long id) throws Exception {
        City city = cityRepository.findById(id).orElseThrow(
                ()-> new Exception("city not exit with given id")
        );
        cityRepository.delete(city);
    }

    @Override
    public Page<CityResponse> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(CityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> searchCities(String keyword, Pageable pageable) {
        return cityRepository.searchByKeyword(keyword,pageable).map(CityMapper::toResponse);
    }

    @Override
    public Page<CityResponse> getCitiesByCountryCode(String countryCode, Pageable pageable) {
        return cityRepository.findByCountryCodeIgnoreCase(countryCode, pageable)
                .map(CityMapper::toResponse);
    }

    @Override
    public boolean cityExists(String cityCode) {
        return cityRepository.existsByCityCode(cityCode);
    }


}
