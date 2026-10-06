package com.example.end_of_term.service;

import com.example.end_of_term.entity.Hall;

import java.util.List;

public interface HallService {
    List<Hall> listAll();
    List<Hall> listByCinemaId(Long cinemaId);
    Hall getById(Long hallId);
    void add(Hall hall);
    void update(Hall hall);
    void delete(Long hallId);
}
