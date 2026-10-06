package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.entity.Cinema;
import com.example.end_of_term.entity.Coupon;
import com.example.end_of_term.entity.Hall;
import com.example.end_of_term.entity.Movie;
import com.example.end_of_term.entity.OrderTicket;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.service.CinemaService;
import com.example.end_of_term.service.CouponService;
import com.example.end_of_term.service.HallService;
import com.example.end_of_term.service.MovieService;
import com.example.end_of_term.service.OrderService;
import com.example.end_of_term.service.ScreeningService;
import com.example.end_of_term.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "管理后台模块")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private HallService hallService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDirConfig;

    @Operation(summary = "添加电影")
    @PostMapping("/movie")
    public Result<Movie> addMovie(@RequestBody Movie movie) {
        return Result.success(movieService.add(movie));
    }

    @Operation(summary = "更新电影")
    @PutMapping("/movie")
    public Result<Void> updateMovie(@RequestBody Movie movie) {
        movieService.update(movie);
        return Result.success();
    }

    @Operation(summary = "删除电影")
    @DeleteMapping("/movie/{movieId}")
    public Result<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.delete(movieId);
        return Result.success();
    }

    @Operation(summary = "添加影院")
    @PostMapping("/cinema")
    public Result<Void> addCinema(@RequestBody Cinema cinema) {
        cinemaService.add(cinema);
        return Result.success();
    }

    @Operation(summary = "更新影院")
    @PutMapping("/cinema")
    public Result<Void> updateCinema(@RequestBody Cinema cinema) {
        cinemaService.update(cinema);
        return Result.success();
    }

    @Operation(summary = "删除影院")
    @DeleteMapping("/cinema/{cinemaId}")
    public Result<Void> deleteCinema(@PathVariable Long cinemaId) {
        cinemaService.delete(cinemaId);
        return Result.success();
    }

    @Operation(summary = "添加影厅")
    @PostMapping("/hall")
    public Result<Void> addHall(@RequestBody Hall hall) {
        hallService.add(hall);
        return Result.success();
    }

    @Operation(summary = "更新影厅")
    @PutMapping("/hall")
    public Result<Void> updateHall(@RequestBody Hall hall) {
        hallService.update(hall);
        return Result.success();
    }

    @Operation(summary = "删除影厅")
    @DeleteMapping("/hall/{hallId}")
    public Result<Void> deleteHall(@PathVariable Long hallId) {
        hallService.delete(hallId);
        return Result.success();
    }

    @Operation(summary = "获取所有影厅")
    @GetMapping("/hall/list")
    public Result<List<Hall>> listHalls(@RequestParam(required = false) Long cinemaId) {
        if (cinemaId != null) {
            return Result.success(hallService.listByCinemaId(cinemaId));
        }
        return Result.success(hallService.listAll());
    }

    @Operation(summary = "添加场次")
    @PostMapping("/screening")
    public Result<Void> addScreening(@RequestBody Screening screening) {
        screeningService.add(screening);
        return Result.success();
    }

    @Operation(summary = "更新场次")
    @PutMapping("/screening")
    public Result<Void> updateScreening(@RequestBody Screening screening) {
        screeningService.update(screening);
        return Result.success();
    }

    @Operation(summary = "删除场次")
    @DeleteMapping("/screening/{screeningId}")
    public Result<Void> deleteScreening(@PathVariable Long screeningId) {
        screeningService.delete(screeningId);
        return Result.success();
    }

    @Operation(summary = "上传文件（电影海报等）")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error(400, "文件名无效");
        }
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = originalFilename.substring(dotIndex).toLowerCase();
        }
        String newFilename = UUID.randomUUID().toString() + ext;

        Path uploadDir = Paths.get(uploadDirConfig);
        if (!uploadDir.isAbsolute()) {
            uploadDir = Paths.get(System.getProperty("user.dir"), uploadDirConfig).toAbsolutePath().normalize();
        }
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path targetPath = uploadDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            return Result.error(500, "文件上传失败: " + e.getMessage());
        }

        String url = "/uploads/" + newFilename;
        return Result.success(Map.of("url", url, "filename", newFilename));
    }

    @Operation(summary = "给指定用户添加优惠券")
    @PostMapping("/coupon/add")
    public Result<Void> addCoupons(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        int count = Integer.parseInt(body.get("count").toString());
        couponService.addCoupons(userId, amount, count);
        return Result.success();
    }

    @Operation(summary = "查询用户优惠券")
    @GetMapping("/coupon/list")
    public Result<List<Coupon>> listCoupons(@RequestParam Long userId) {
        return Result.success(couponService.getByUserId(userId));
    }

    // ==================== 订单管理（后台） ====================

    @Operation(summary = "获取所有订单")
    @GetMapping("/order/list")
    public Result<List<OrderTicket>> listAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    @Operation(summary = "按用户查询订单")
    @GetMapping("/order/list-by-user")
    public Result<List<OrderTicket>> listOrdersByUser(@RequestParam Long userId) {
        return Result.success(orderService.getMyOrders(userId));
    }

    @Operation(summary = "获取所有用户ID列表")
    @GetMapping("/order/users")
    public Result<List<Long>> getAllUserIds() {
        return Result.success(orderService.getAllUserIds());
    }

    @Operation(summary = "删除用户及所有订单")
    @DeleteMapping("/order/user/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return Result.success();
    }

    @Operation(summary = "删除指定订单")
    @DeleteMapping("/order/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return Result.success();
    }

    @Operation(summary = "验证管理员权限")
    @GetMapping("/check")
    public Result<Void> checkAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return Result.error(403, "需要管理员权限");
        }
        return Result.success();
    }
}
