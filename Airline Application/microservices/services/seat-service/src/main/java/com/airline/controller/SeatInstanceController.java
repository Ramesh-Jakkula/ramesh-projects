package com.airline.controller;

import com.airline.service.SeatInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-instances")
@RequiredArgsConstructor
public class SeatInstanceController {

    private final SeatInstanceService seatInstanceService;

    @PostMapping("/price/price")
    public Double calculateSeatPrice(@RequestBody List<Long> seatInstanceIds) {
        return seatInstanceService.calculateSeatPrice(seatInstanceIds);
    }
}
