package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.entity.Screening;
import com.example.end_of_term.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "场次模块")
@RestController
@RequestMapping("/api/screening")
public class ScreeningController {

    @Autowired
    private ScreeningService screeningService;

    @Operation(summary = "获取全部场次")
    @GetMapping("/list")
    public Result<List<Screening>> listAll() {
        return Result.success(screeningService.listAll());
    }

    @Operation(summary = "根据电影ID获取场次列表")
    @GetMapping("/byMovie/{movieId}")
    public Result<List<Screening>> listByMovieId(@PathVariable Long movieId) {
        return Result.success(screeningService.listByMovieId(movieId));
    }

    @Operation(summary = "根据影厅ID获取场次列表")
    @GetMapping("/byHall/{hallId}")
    public Result<List<Screening>> listByHallId(@PathVariable Long hallId) {
        return Result.success(screeningService.listByHallId(hallId));
    }

    @Operation(summary = "获取场次详情")
    @GetMapping("/{screeningId}")
    public Result<Screening> getById(@PathVariable Long screeningId) {
        return Result.success(screeningService.getById(screeningId));
    }
}
