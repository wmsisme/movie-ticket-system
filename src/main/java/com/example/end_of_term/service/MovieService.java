package com.example.end_of_term.service;

import com.example.end_of_term.entity.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> listAll();
    List<Movie> listByStatus(String status);
    Movie getById(Long movieId);
    Movie add(Movie movie);
    void update(Movie movie);
    void delete(Long movieId);
}
