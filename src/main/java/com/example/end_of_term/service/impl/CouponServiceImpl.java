package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.Coupon;
import com.example.end_of_term.mapper.CouponMapper;
import com.example.end_of_term.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<Coupon> getByUserId(Long userId) {
        return couponMapper.selectByUserId(userId);
    }

    @Override
    public List<Coupon> getAvailableByUserId(Long userId) {
        return couponMapper.selectAvailableByUserId(userId);
    }

    @Override
    @Transactional
    public void addCoupons(Long userId, BigDecimal amount, int count) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("优惠券金额不能为0");
        }
        if (count <= 0 || count > 100) {
            throw new BusinessException("数量必须在1-100之间");
        }

        List<Coupon> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Coupon coupon = new Coupon();
            coupon.setUserId(userId);
            coupon.setAmount(amount);
            list.add(coupon);
        }
        couponMapper.batchInsert(list);
    }
}
