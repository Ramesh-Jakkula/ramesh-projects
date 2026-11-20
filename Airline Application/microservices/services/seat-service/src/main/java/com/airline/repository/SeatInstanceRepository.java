package com.airline.repository;

import com.airline.model.SeatInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatInstanceRepository extends JpaRepository<SeatInstance, Long> {

}
