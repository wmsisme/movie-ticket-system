package com.example.end_of_term.config;

import com.example.end_of_term.entity.*;
import com.example.end_of_term.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private SeatMapper seatMapper;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private ScreeningMapper screeningMapper;

    @Override
    public void run(String... args) {
        List<Cinema> existingCinemas = cinemaMapper.selectAll();
        if (existingCinemas != null && !existingCinemas.isEmpty()) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("开始初始化电影购票系统数据...");

        initCinemasAndHalls();
        initMovies();
        initScreenings();

        log.info("数据初始化完成");
    }

    private void initCinemasAndHalls() {
        String[][] cinemaData = {
                {"星光影城（朝阳店）", "北京市朝阳区建国路88号", "北京", "010-88886666"},
                {"万达影城（五棵松店）", "北京市海淀区复兴路69号", "北京", "010-66668888"},
                {"CGV影城（上海陆家嘴店）", "上海市浦东新区世纪大道100号", "上海", "021-55558888"},
                {"金逸影城（广州天河店）", "广州市天河区天河路208号", "广州", "020-33336666"},
        };

        int[][] hallConfigs = {
                {8, 10}, {8, 12}, {6, 8},
                {10, 12}, {7, 10}, {9, 14},
                {8, 12}, {6, 9}, {10, 14},
                {7, 10}, {8, 12}, {9, 12},
        };

        String[] hallTypeNames = {"激光IMAX厅", "杜比全景声厅", "VIP小厅"};

        int hallIdx = 0;
        for (String[] cd : cinemaData) {
            Cinema cinema = new Cinema();
            cinema.setName(cd[0]);
            cinema.setAddress(cd[1]);
            cinema.setCity(cd[2]);
            cinema.setPhone(cd[3]);
            cinemaMapper.insert(cinema);

            for (int j = 0; j < 3 && hallIdx < hallConfigs.length; j++, hallIdx++) {
                Hall hall = new Hall();
                hall.setCinemaId(cinema.getCinemaId());
                hall.setName(hallTypeNames[j] + (j + 1));
                hall.setSeatRows(hallConfigs[hallIdx][0]);
                hall.setSeatCols(hallConfigs[hallIdx][1]);
                hallMapper.insert(hall);

                createSeats(hall.getHallId(), hall.getSeatRows(), hall.getSeatCols());
            }
        }
    }

    private void createSeats(Long hallId, int rows, int cols) {
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                Seat seat = new Seat();
                seat.setHallId(hallId);
                seat.setRowNum(r);
                seat.setColNum(c);
                seat.setSeatCode(String.format("%02d排%02d座", r, c));
                seat.setSeatType(r <= 3 ? 1 : 0);
                seat.setStatus(0);
                seatMapper.insert(seat);
            }
        }
    }

    private void initMovies() {
        String[][] movieData = {
                {"流浪地球3", "郭帆", "吴京,刘德华,李雪健", "科幻,冒险,灾难", "150", "2027-02-10",
                        "太阳急速老化，持续膨胀。100年后，太阳将膨胀到地球轨道。三万名宇航员奉命执行【流浪地球】计划……",
                        "/wandering-earth.jpg", "8.8"},
                {"哪吒之魔童闹海", "饺子", "吕艳婷,囧森瑟夫,瀚墨", "动画,奇幻,动作", "144", "2025-01-29",
                        "天劫之后，哪吒、敖丙的灵魂虽保住了，但肉身很快会魂飞魄散。太乙真人打算用七色宝莲给二人重塑肉身……",
                        "/nezha.jpg", "8.5"},
                {"封神第二部：战火西岐", "乌尔善", "费翔,黄渤,于适", "奇幻,动作,战争", "150", "2025-07-01",
                        "西岐保卫战正式打响，姜子牙率众神将迎战殷商大军……",
                        "/nezha.jpg", "8.2"},
                {"唐人街探案4", "陈思诚", "王宝强,刘昊然", "喜剧,悬疑,动作", "135", "2026-01-24",
                        "唐仁和秦风前往东京调查一桩离奇案件，却发现背后隐藏着更大的阴谋……",
                        "/nezha.jpg", "7.8"},
                {"寻秦记", "许宏宇", "古天乐,林峯,宣萱", "动作,冒险,科幻", "120", "2025-10-01",
                        "项少龙意外穿越时空回到战国时代，卷入了一场惊心动魄的权力斗争……",
                        "/nezha.jpg", "7.5"},
                {"深海2", "田晓鹏", "苏鑫,王亭文", "动画,奇幻,冒险", "112", "2027-01-29",
                        "参宿再次进入神秘的深海世界，这次她将面对更加奇幻的冒险和内心的成长……",
                        "/nezha.jpg", "8.0"},
        };

        for (String[] md : movieData) {
            Movie movie = new Movie();
            movie.setTitle(md[0]);
            movie.setDirector(md[1]);
            movie.setCast(md[2]);
            movie.setGenre(md[3]);
            movie.setDuration(Integer.parseInt(md[4]));
            movie.setReleaseDate(LocalDate.parse(md[5]));
            movie.setStatus(2);
            movie.setDescription(md[6]);
            movie.setPosterUrl(md[7]);
            movie.setRating(new BigDecimal(md[8]));
            movieMapper.insert(movie);
        }
    }

    private void initScreenings() {
        List<Movie> movies = movieMapper.selectAll();
        List<Hall> halls = hallMapper.selectAll();

        if (movies.isEmpty() || halls.isEmpty()) return;

        BigDecimal[] priceTiers = {new BigDecimal("39.90"), new BigDecimal("49.90"), new BigDecimal("59.90")};
        String[] timeSlots = {"09:30", "12:00", "14:30", "17:00", "19:30", "22:00"};
        LocalDate today = LocalDate.now();

        for (int i = 0; i < movies.size(); i++) {
            for (int day = 0; day < 3; day++) {
                for (int slot = 0; slot < 3; slot++) {
                    Screening scr = new Screening();
                    scr.setMovieId(movies.get(i).getMovieId());
                    int hallIdx = (i + day * slot) % halls.size();
                    scr.setHallId(halls.get(hallIdx).getHallId());
                    scr.setScreeningDate(today.plusDays(day));
                    LocalTime start = LocalTime.parse(timeSlots[slot * 2]);
                    scr.setStartTime(start);
                    int duration = movies.get(i).getDuration() != null ? movies.get(i).getDuration() : 120;
                    scr.setEndTime(start.plusMinutes(duration + 15));
                    scr.setPrice(priceTiers[hallIdx % priceTiers.length]);
                    scr.setVersion(hallIdx % 2 == 0 ? "2D" : "IMAX 3D");
                    scr.setLanguage("国语");
                    screeningMapper.insert(scr);
                }
            }
        }
    }
}
