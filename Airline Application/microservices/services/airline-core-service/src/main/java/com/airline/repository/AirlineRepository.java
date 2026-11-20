package com.airline.repository;

import com.airline.enums.AirlineStatus;
import com.airline.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findByOwnerId(Long ownerId);
    List<Airline> findByStatus(AirlineStatus status);
    boolean existsByOwnerId(Long ownerId);
}
