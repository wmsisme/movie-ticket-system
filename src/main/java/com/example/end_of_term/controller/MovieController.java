package com.example.end_of_term.controller;

import com.example.end_of_term.common.Result;
import com.example.end_of_term.entity.Movie;
import com.example.end_of_term.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "电影模块")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Operation(summary = "获取电影列表（支持按状态筛选）")
    @GetMapping("/list")
    public Result<List<Movie>> list(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return Result.success(movieService.listByStatus(status));
        }
        return Result.success(movieService.listAll());
    }

    @Operation(summary = "获取电影详情")
    @GetMapping("/{movieId}")
    public Result<Movie> getById(@PathVariable Long movieId) {
        return Result.success(movieService.getById(movieId));
    }
}
