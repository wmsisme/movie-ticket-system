package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Movie;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.MovieMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.service.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScreeningServiceImpl implements ScreeningService {

    @Autowired
    private ScreeningMapper screeningMapper;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private OrderTicketMapper orderTicketMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Override
    public List<Screening> listAll() {
        return screeningMapper.selectAll();
    }

    @Override
    public List<Screening> listByMovieId(Long movieId) {
        return screeningMapper.selectByMovieId(movieId);
    }

    @Override
    public List<Screening> listByHallId(Long hallId) {
        return screeningMapper.selectByHallId(hallId);
    }

    @Override
    public Screening getById(Long screeningId) {
        Screening screening = screeningMapper.selectById(screeningId);
        if (screening == null) {
            throw new BusinessException("场次不存在");
        }
        return screening;
    }

    @Override
    public void add(Screening screening) {
        Movie movie = movieMapper.selectById(screening.getMovieId());
        if (movie != null && movie.getStatus() == 1) {
            throw new BusinessException("即将上映的电影无法添加排片");
        }
        screeningMapper.insert(screening);
    }

    @Override
    public void update(Screening screening) {
        Movie movie = movieMapper.selectById(screening.getMovieId());
        if (movie != null && movie.getStatus() == 1) {
            throw new BusinessException("即将上映的电影无法编辑排片");
        }
        screeningMapper.update(screening);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long screeningId) {
        orderSeatMapper.deleteByScreeningId(screeningId);
        orderTicketMapper.deleteByScreeningId(screeningId);
        screeningMapper.deleteById(screeningId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByMovieId(Long movieId) {
        List<Screening> screenings = screeningMapper.selectByMovieId(movieId);
        for (Screening scr : screenings) {
            orderSeatMapper.deleteByScreeningId(scr.getScreeningId());
            orderTicketMapper.deleteByScreeningId(scr.getScreeningId());
        }
        screeningMapper.deleteByMovieId(movieId);
    }
}
