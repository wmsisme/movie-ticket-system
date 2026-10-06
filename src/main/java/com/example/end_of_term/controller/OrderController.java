package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.dto.CreateOrderRequest;
import com.example.end_of_term.entity.Coupon;
import com.example.end_of_term.entity.OrderSeat;
import com.example.end_of_term.entity.OrderTicket;
import com.example.end_of_term.service.CouponService;
import com.example.end_of_term.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderTicket> createOrder(@RequestBody CreateOrderRequest request, HttpSession session) {
        Long userId = getUserIdFromSession(session);
        return Result.success(orderService.createOrder(userId, request.getScreeningId(), request.getSeatIds()));
    }

    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public Result<List<OrderTicket>> getMyOrders(HttpSession session) {
        Long userId = getUserIdFromSession(session);
        return Result.success(orderService.getMyOrders(userId));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderTicket> getOrderDetail(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(orderId));
    }

    @Operation(summary = "获取订单座位明细")
    @GetMapping("/{orderId}/seats")
    public Result<List<OrderSeat>> getOrderSeats(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderSeats(orderId));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId, HttpSession session) {
        Long userId = getUserIdFromSession(session);
        orderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    @Operation(summary = "支付订单（模拟）")
    @PutMapping("/pay/{orderId}")
    public Result<Void> payOrder(@PathVariable Long orderId, HttpSession session,
                                 @RequestParam(required = false) Long couponId) {
        Long userId = getUserIdFromSession(session);
        orderService.payOrder(orderId, userId, couponId);
        return Result.success();
    }

    @Operation(summary = "获取当前用户可用优惠券")
    @GetMapping("/coupons")
    public Result<List<Coupon>> getAvailableCoupons(HttpSession session) {
        Long userId = getUserIdFromSession(session);
        return Result.success(couponService.getAvailableByUserId(userId));
    }

    private Long getUserIdFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }
}
