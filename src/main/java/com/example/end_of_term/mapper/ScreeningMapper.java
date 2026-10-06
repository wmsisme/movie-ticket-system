package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Screening;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScreeningMapper {
    List<Screening> selectAll();
    List<Screening> selectByMovieId(@Param("movieId") Long movieId);
    List<Screening> selectByHallId(@Param("hallId") Long hallId);
    Screening selectById(@Param("screeningId") Long screeningId);
    int insert(Screening screening);
    int update(Screening screening);
    int deleteById(@Param("screeningId") Long screeningId);
    int deleteByMovieId(@Param("movieId") Long movieId);
    int deleteByHallId(@Param("hallId") Long hallId);
}
