package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.OrderSeat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderSeatMapper {
    List<OrderSeat> selectByOrderId(@Param("orderId") Long orderId);
    int insert(OrderSeat orderSeat);
    int deleteByOrderId(@Param("orderId") Long orderId);
    int deleteByUserId(@Param("userId") Long userId);
    int deleteByScreeningId(@Param("screeningId") Long screeningId);
}
