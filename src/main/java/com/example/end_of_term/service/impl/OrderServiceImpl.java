package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Coupon;
import com.example.end_of_term.entity.OrderSeat;
import com.example.end_of_term.entity.OrderTicket;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.mapper.CouponMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.ScreeningMapper;
import com.example.end_of_term.mapper.SeatMapper;
import com.example.end_of_term.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_PAID = 1;
    private static final int STATUS_CANCELLED = 2;

    @Autowired
    private OrderTicketMapper orderTicketMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Autowired
    private ScreeningMapper screeningMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private CouponMapper couponMapper;

    private static final int MAX_SEATS_PER_SCREENING = 5;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 定时任务：每分钟执行一次，取消超过10分钟未支付的订单
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void cancelExpiredOrders() {
        List<Long> expiredOrderIds = orderTicketMapper.selectExpiredPendingOrders();
        if (expiredOrderIds.isEmpty()) {
            return;
        }
        log.info("发现 {} 个超时未支付订单，正在自动取消", expiredOrderIds.size());
        for (Long orderId : expiredOrderIds) {
            orderSeatMapper.deleteByOrderId(orderId);
            orderTicketMapper.updateStatus(orderId, STATUS_CANCELLED);
        }
        log.info("已自动取消 {} 个超时订单", expiredOrderIds.size());
    }

    @Override
    @Transactional
    public OrderTicket createOrder(Long userId, Long screeningId, List<Long> seatIds) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (screeningId == null) {
            throw new BusinessException("场次ID不能为空");
        }
        if (seatIds == null || seatIds.isEmpty()) {
            throw new BusinessException("请至少选择一个座位");
        }
        if (seatIds.size() > MAX_SEATS_PER_SCREENING) {
            throw new BusinessException("单次最多购买 " + MAX_SEATS_PER_SCREENING + " 张票");
        }

        Screening screening = screeningMapper.selectById(screeningId);
        if (screening == null) {
            throw new BusinessException("场次不存在");
        }

        int alreadyBought = orderTicketMapper.countByUserIdAndScreeningIdNotCancelled(userId, screeningId);
        if (alreadyBought >= MAX_SEATS_PER_SCREENING) {
            throw new BusinessException("您在本场次已购买 " + alreadyBought + " 张票，已达到上限 " + MAX_SEATS_PER_SCREENING + " 张");
        }
        if (alreadyBought + seatIds.size() > MAX_SEATS_PER_SCREENING) {
            throw new BusinessException("您在本场次已购买 " + alreadyBought + " 张票，本次选择 " + seatIds.size() + " 张，超出单场限购 " + MAX_SEATS_PER_SCREENING + " 张");
        }

        List<com.example.end_of_term.entity.Seat> availableSeats = seatMapper.selectAvailableByScreeningId(
                screening.getHallId(), screeningId);

        for (Long seatId : seatIds) {
            boolean found = availableSeats.stream().anyMatch(s -> s.getSeatId().equals(seatId));
            if (!found) {
                throw new BusinessException("座位 " + seatId + " 已被占用或不可选");
            }
        }

        BigDecimal totalPrice = screening.getPrice().multiply(new BigDecimal(seatIds.size()));

        String orderNumber = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        OrderTicket order = new OrderTicket();
        order.setUserId(userId);
        order.setScreeningId(screeningId);
        order.setOrderNumber(orderNumber);
        order.setTotalPrice(totalPrice);
        order.setStatus(STATUS_PENDING);
        orderTicketMapper.insert(order);

        for (Long seatId : seatIds) {
            OrderSeat orderSeat = new OrderSeat();
            orderSeat.setOrderId(order.getOrderId());
            orderSeat.setSeatId(seatId);
            orderSeat.setPrice(screening.getPrice());
            orderSeatMapper.insert(orderSeat);
        }

        return order;
    }

    @Override
    public List<OrderTicket> getMyOrders(Long userId) {
        return orderTicketMapper.selectByUserId(userId);
    }

    @Override
    public OrderTicket getOrderDetail(Long orderId) {
        OrderTicket order = orderTicketMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    @Override
    public List<OrderSeat> getOrderSeats(Long orderId) {
        return orderSeatMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        OrderTicket order = orderTicketMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("只能取消待支付状态的订单");
        }
        orderSeatMapper.deleteByOrderId(orderId);
        orderTicketMapper.updateStatus(orderId, STATUS_CANCELLED);
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, Long userId, Long couponId) {
        OrderTicket order = orderTicketMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != STATUS_PENDING) {
            throw new BusinessException("只能支付待支付状态的订单");
        }

        if (couponId != null) {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                throw new BusinessException("优惠券不存在");
            }
            if (!coupon.getUserId().equals(userId)) {
                throw new BusinessException("优惠券不属于当前用户");
            }
            if (coupon.getIsUsed() == 1) {
                throw new BusinessException("优惠券已被使用");
            }

            BigDecimal discountedPrice = order.getTotalPrice().subtract(coupon.getAmount());
            if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                discountedPrice = BigDecimal.ZERO;
            }
            orderTicketMapper.updateTotalPrice(orderId, discountedPrice);
            couponMapper.markUsed(couponId);
        }

        orderTicketMapper.updateStatus(orderId, STATUS_PAID);
    }

    @Override
    public List<Long> getAllUserIds() {
        return orderTicketMapper.selectDistinctUserIds();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        orderSeatMapper.deleteByUserId(userId);
        orderTicketMapper.deleteByUserId(userId);
    }

    @Override
    public List<OrderTicket> getAllOrders() {
        return orderTicketMapper.selectAll();
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        orderSeatMapper.deleteByOrderId(orderId);
        orderTicketMapper.deleteById(orderId);
    }
}
