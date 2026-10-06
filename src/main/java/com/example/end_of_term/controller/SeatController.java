package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.entity.Seat;
import com.example.end_of_term.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "座位模块")
@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Operation(summary = "获取某场次的可选座位")
    @GetMapping("/available/{screeningId}")
    public Result<List<Seat>> getAvailableSeats(@PathVariable Long screeningId) {
        return Result.success(seatService.getAvailableSeats(screeningId));
    }

    @Operation(summary = "获取某影厅的全部座位")
    @GetMapping("/hall/{hallId}")
    public Result<List<Seat>> getByHallId(@PathVariable Long hallId) {
        return Result.success(seatService.getByHallId(hallId));
    }
}
