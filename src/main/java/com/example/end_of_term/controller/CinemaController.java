package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.entity.Cinema;
import com.example.end_of_term.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "影院模块")
@RestController
@RequestMapping("/api/cinema")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @Operation(summary = "获取影院列表（支持按城市筛选）")
    @GetMapping("/list")
    public Result<List<Cinema>> list(@RequestParam(required = false) String city) {
        if (city != null && !city.isEmpty()) {
            return Result.success(cinemaService.listByCity(city));
        }
        return Result.success(cinemaService.listAll());
    }

    @Operation(summary = "获取影院详情")
    @GetMapping("/{cinemaId}")
    public Result<Cinema> getById(@PathVariable Long cinemaId) {
        return Result.success(cinemaService.getById(cinemaId));
    }
}
