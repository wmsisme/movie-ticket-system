package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Cinema;
import com.example.end_of_term.entity.Hall;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.CinemaMapper;
import com.example.end_of_term.mapper.HallMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.mapper.SeatMapper;
import com.example.end_of_term.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private SeatMapper seatMapper;
    @Autowired
    private ScreeningMapper screeningMapper;
    @Autowired
    private OrderTicketMapper orderTicketMapper;
    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Override
    public List<Cinema> listAll() {
        return cinemaMapper.selectAll();
    }

    @Override
    public List<Cinema> listByCity(String city) {
        return cinemaMapper.selectByCity(city);
    }

    @Override
    public Cinema getById(Long cinemaId) {
        Cinema cinema = cinemaMapper.selectById(cinemaId);
        if (cinema == null) {
            throw new BusinessException("影院不存在");
        }
        return cinema;
    }

    @Override
    public void add(Cinema cinema) {
        cinemaMapper.insert(cinema);
    }

    @Override
    public void update(Cinema cinema) {
        cinemaMapper.update(cinema);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long cinemaId) {
        // 1. 找到该影院的所有影厅
        List<Hall> halls = hallMapper.selectByCinemaId(cinemaId);

        for (Hall hall : halls) {
            Long hallId = hall.getHallId();

            // 2. 找到该影厅的所有场次
            List<Screening> screenings = screeningMapper.selectByHallId(hallId);

            // 3. 先删除每个场次关联的订单座位和订单
            for (Screening scr : screenings) {
                orderSeatMapper.deleteByScreeningId(scr.getScreeningId());
                orderTicketMapper.deleteByScreeningId(scr.getScreeningId());
            }

            // 4. 删除该影厅的所有场次
            screeningMapper.deleteByHallId(hallId);

            // 5. 删除该影厅的所有座位
            seatMapper.deleteByHallId(hallId);
        }

        // 6. 删除该影院的所有影厅
        hallMapper.deleteByCinemaId(cinemaId);

        // 7. 删除影院
        cinemaMapper.deleteById(cinemaId);
    }
}
