package com.example.end_of_term.service;

import com.example.end_of_term.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    Map<String, Object> adminLogin(String username, String password, Long currentUserId);
    User getById(Long userId);
    List<User> listAll();
    void update(User user);
    void delete(Long userId);
}
