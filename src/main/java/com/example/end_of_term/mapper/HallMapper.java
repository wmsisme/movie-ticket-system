package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Hall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HallMapper {
    List<Hall> selectAll();
    List<Hall> selectByCinemaId(@Param("cinemaId") Long cinemaId);
    Hall selectById(@Param("hallId") Long hallId);
    int insert(Hall hall);
    int update(Hall hall);
    int deleteById(@Param("hallId") Long hallId);
    int deleteByCinemaId(@Param("cinemaId") Long cinemaId);
}
