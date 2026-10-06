package com.example.end_of_term.service;

import com.example.end_of_term.entity.OrderSeat;
import com.example.end_of_term.entity.OrderTicket;

import java.util.List;

public interface OrderService {
    OrderTicket createOrder(Long userId, Long screeningId, List<Long> seatIds);
    List<OrderTicket> getMyOrders(Long userId);
    OrderTicket getOrderDetail(Long orderId);
    List<OrderSeat> getOrderSeats(Long orderId);
    void cancelOrder(Long orderId, Long userId);
    void payOrder(Long orderId, Long userId, Long couponId);
    List<Long> getAllUserIds();
    void deleteUser(Long userId);
    void cancelExpiredOrders();
    List<OrderTicket> getAllOrders();
    void deleteOrder(Long orderId);
}
