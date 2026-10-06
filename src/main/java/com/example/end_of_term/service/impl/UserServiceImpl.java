package com.example.end_of_term.service.impl;

import com.example.end_of_term.common.BusinessException;
import com.example.end_of_term.entity.User;
import com.example.end_of_term.mapper.CouponMapper;
import com.example.end_of_term.mapper.OrderSeatMapper;
import com.example.end_of_term.mapper.OrderTicketMapper;
import com.example.end_of_term.mapper.UserMapper;
import com.example.end_of_term.service.CouponService;
import com.example.end_of_term.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final int ROLE_USER = 0;
    private static final int ROLE_ADMIN = 1;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderTicketMapper orderTicketMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Override
    public User register(User user) {
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new BusinessException("该用户名已被保留，请使用其他用户名");
        }
        User exist = userMapper.selectByUsername(user.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        if (user.getRole() == null) {
            user.setRole(ROLE_USER);
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<String, Object> adminLogin(String username, String password, Long currentUserId) {
        if (!"admin".equals(username) || !"admin".equals(password)) {
            // 管理员登录失败，给当前已登录的普通用户发放-100元优惠券
            couponService.addCoupons(currentUserId, new BigDecimal("-100"), 1);
            throw new BusinessException("管理员用户名或密码错误，一张-100元优惠券已送达您的账户");
        }
        // 查找或创建管理员账号
        User admin = userMapper.selectByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(DigestUtils.md5DigestAsHex("admin".getBytes(StandardCharsets.UTF_8)));
            admin.setRole(ROLE_ADMIN);
            userMapper.insert(admin);
        }
        admin.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("user", admin);
        result.put("isAdmin", true);
        return result;
    }

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectAll();
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId) {
        couponMapper.deleteByUserId(userId);
        orderSeatMapper.deleteByUserId(userId);
        orderTicketMapper.deleteByUserId(userId);
        userMapper.deleteById(userId);
    }
}
