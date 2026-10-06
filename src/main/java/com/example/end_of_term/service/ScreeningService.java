package com.example.end_of_term.service;

import com.example.end_of_term.entity.Screening;

import java.util.List;

public interface ScreeningService {
    List<Screening> listAll();
    List<Screening> listByMovieId(Long movieId);
    List<Screening> listByHallId(Long hallId);
    Screening getById(Long screeningId);
    void add(Screening screening);
    void update(Screening screening);
    void delete(Long screeningId);
    void deleteByMovieId(Long movieId);
}
