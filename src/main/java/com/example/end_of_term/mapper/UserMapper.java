package com.example.end_of_term.mapper;

import com.example.end_of_term.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAll();
    User selectById(@Param("userId") Long userId);
    User selectByUsername(@Param("username") String username);
    int insert(User user);
    int update(User user);
    int deleteById(@Param("userId") Long userId);
}
