package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeatMapper {
    List<Seat> selectByHallId(@Param("hallId") Long hallId);
    List<Seat> selectAvailableByScreeningId(@Param("hallId") Long hallId, @Param("screeningId") Long screeningId);
    Seat selectById(@Param("seatId") Long seatId);
    int insert(Seat seat);
    int update(Seat seat);
    int deleteById(@Param("seatId") Long seatId);
    int deleteByHallId(@Param("hallId") Long hallId);
}
