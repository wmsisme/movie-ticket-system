package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Seat;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.mapper.SeatMapper;
import com.example.end_of_term.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ScreeningMapper screeningMapper;

    @Override
    public List<Seat> getAvailableSeats(Long screeningId) {
        Screening screening = screeningMapper.selectById(screeningId);
        if (screening == null) {
            throw new BusinessException("场次不存在");
        }
        return seatMapper.selectAvailableByScreeningId(screening.getHallId(), screeningId);
    }

    @Override
    public List<Seat> getByHallId(Long hallId) {
        return seatMapper.selectByHallId(hallId);
    }
}
