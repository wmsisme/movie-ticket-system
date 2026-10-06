package com.example.end_of_term.service;

import com.example.end_of_term.entity.Cinema;

import java.util.List;

public interface CinemaService {
    List<Cinema> listAll();
    List<Cinema> listByCity(String city);
    Cinema getById(Long cinemaId);
    void add(Cinema cinema);
    void update(Cinema cinema);
    void delete(Long cinemaId);
}
