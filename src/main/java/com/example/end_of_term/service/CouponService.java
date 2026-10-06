package com.example.end_of_term.service;

import com.example.end_of_term.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<Coupon> getByUserId(Long userId);
    List<Coupon> getAvailableByUserId(Long userId);
    void addCoupons(Long userId, BigDecimal amount, int count);
}
