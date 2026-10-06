package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Hall;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.HallMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.mapper.SeatMapper;
import com.example.end_of_term.service.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HallServiceImpl implements HallService {

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
    public List<Hall> listAll() {
        return hallMapper.selectAll();
    }

    @Override
    public List<Hall> listByCinemaId(Long cinemaId) {
        return hallMapper.selectByCinemaId(cinemaId);
    }

    @Override
    public Hall getById(Long hallId) {
        Hall hall = hallMapper.selectById(hallId);
        if (hall == null) {
            throw new BusinessException("影厅不存在");
        }
        return hall;
    }

    @Override
    public void add(Hall hall) {
        hallMapper.insert(hall);
    }

    @Override
    public void update(Hall hall) {
        hallMapper.update(hall);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long hallId) {
        // 先删除该影厅所有场次关联的订单座位和订单
        List<Screening> screenings = screeningMapper.selectByHallId(hallId);
        for (Screening scr : screenings) {
            orderSeatMapper.deleteByScreeningId(scr.getScreeningId());
            orderTicketMapper.deleteByScreeningId(scr.getScreeningId());
        }

        // 删除该影厅的所有场次
        screeningMapper.deleteByHallId(hallId);

        // 删除该影厅的所有座位
        seatMapper.deleteByHallId(hallId);

        // 删除影厅
        hallMapper.deleteById(hallId);
    }
}
