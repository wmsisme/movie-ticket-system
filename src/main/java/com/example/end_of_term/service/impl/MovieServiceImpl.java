package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Movie;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.MovieMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ScreeningMapper screeningMapper;

    @Autowired
    private OrderTicketMapper orderTicketMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Override
    public List<Movie> listAll() {
        return movieMapper.selectAll();
    }

    @Override
    public List<Movie> listByStatus(String status) {
        return movieMapper.selectByStatus(status);
    }

    @Override
    public Movie getById(Long movieId) {
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) {
            throw new BusinessException("电影不存在");
        }
        return movie;
    }

    @Override
    public Movie add(Movie movie) {
        if (movie.getStatus() == null) {
            movie.setStatus(2);
        }
        movieMapper.insert(movie);
        return movie;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Movie movie) {
        // 如果状态改为即将上映(1)，删除该电影的所有排片及关联订单
        if (movie.getStatus() != null && movie.getStatus() == 1) {
            List<Screening> screenings = screeningMapper.selectByMovieId(movie.getMovieId());
            for (Screening scr : screenings) {
                orderSeatMapper.deleteByScreeningId(scr.getScreeningId());
                orderTicketMapper.deleteByScreeningId(scr.getScreeningId());
            }
            screeningMapper.deleteByMovieId(movie.getMovieId());
        }
        movieMapper.update(movie);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long movieId) {
        // 先清理该电影所有场次关联的订单座位和订单
        List<Screening> screenings = screeningMapper.selectByMovieId(movieId);
        for (Screening scr : screenings) {
            orderSeatMapper.deleteByScreeningId(scr.getScreeningId());
            orderTicketMapper.deleteByScreeningId(scr.getScreeningId());
        }

        // 删除该电影的所有场次
        screeningMapper.deleteByMovieId(movieId);

        // 删除电影
        movieMapper.deleteById(movieId);
    }
}
