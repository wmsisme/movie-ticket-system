package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.OrderTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderTicketMapper {
    List<OrderTicket> selectByUserId(@Param("userId") Long userId);
    OrderTicket selectById(@Param("orderId") Long orderId);
    int insert(OrderTicket orderTicket);
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
    int updateTotalPrice(@Param("orderId") Long orderId, @Param("totalPrice") java.math.BigDecimal totalPrice);
    int deleteById(@Param("orderId") Long orderId);
    int countByUserIdAndScreeningIdNotCancelled(@Param("userId") Long userId, @Param("screeningId") Long screeningId);
    List<Long> selectDistinctUserIds();
    int deleteByUserId(@Param("userId") Long userId);
    int deleteByScreeningId(@Param("screeningId") Long screeningId);
    List<Long> selectExpiredPendingOrders();
    List<OrderTicket> selectAll();
}
