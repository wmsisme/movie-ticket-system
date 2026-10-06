package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.dto.LoginRequest;
import com.example.end_of_term.entity.User;
import com.example.end_of_term.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user, HttpSession session) {
        User registered = userService.register(user);
        session.setAttribute("userId", registered.getUserId());
        session.setAttribute("username", registered.getUsername());
        session.setAttribute("isAdmin", false);
        return Result.success(registered);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request.getUsername(), request.getPassword());
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("isAdmin", false);
        return Result.success(user);
    }

    @Operation(summary = "管理员登录（需先登录普通用户）")
    @PostMapping("/admin-login")
    public Result<Map<String, Object>> adminLogin(@RequestBody LoginRequest request, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return Result.error(401, "请先登录普通用户账号");
        }
        Map<String, Object> result = userService.adminLogin(request.getUsername(), request.getPassword(), currentUserId);
        User admin = (User) result.get("user");
        session.setAttribute("userId", admin.getUserId());
        session.setAttribute("username", admin.getUsername());
        session.setAttribute("isAdmin", true);
        return Result.success(result);
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        String username = (String) session.getAttribute("username");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Map<String, Object> info = Map.of(
                "userId", userId,
                "username", username != null ? username : "",
                "isAdmin", isAdmin != null && isAdmin
        );
        return Result.success(info);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{userId}")
    public Result<User> getById(@PathVariable Long userId) {
        return Result.success(userService.getById(userId));
    }
}
