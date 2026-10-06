package com.example.end_of_term.service;

import com.example.end_of_term.entity.Seat;

import java.util.List;

public interface SeatService {
    List<Seat> getAvailableSeats(Long screeningId);
    List<Seat> getByHallId(Long hallId);
}
