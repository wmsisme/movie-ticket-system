package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    List<Movie> selectAll();
    List<Movie> selectByStatus(@Param("status") String status);
    Movie selectById(@Param("movieId") Long movieId);
    int insert(Movie movie);
    int update(Movie movie);
    int deleteById(@Param("movieId") Long movieId);
}
