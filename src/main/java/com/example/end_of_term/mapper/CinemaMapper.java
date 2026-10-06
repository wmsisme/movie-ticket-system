package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Cinema;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CinemaMapper {
    List<Cinema> selectAll();
    List<Cinema> selectByCity(@Param("city") String city);
    Cinema selectById(@Param("cinemaId") Long cinemaId);
    int insert(Cinema cinema);
    int update(Cinema cinema);
    int deleteById(@Param("cinemaId") Long cinemaId);
}
