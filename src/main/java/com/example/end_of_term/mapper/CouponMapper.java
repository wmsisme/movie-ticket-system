package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<Coupon> selectByUserId(@Param("userId") Long userId);
    List<Coupon> selectAvailableByUserId(@Param("userId") Long userId);
    Coupon selectById(@Param("couponId") Long couponId);
    int insert(Coupon coupon);
    int batchInsert(@Param("list") List<Coupon> list);
    int markUsed(@Param("couponId") Long couponId);
    int deleteByUserId(@Param("userId") Long userId);
}
