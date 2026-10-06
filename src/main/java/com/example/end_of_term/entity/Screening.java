package com.example.end_of_term.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Screening {
    private Long screeningId;
    private Long movieId;
    private Long hallId;
    private LocalDate screeningDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal price;
    private String version;
    private String language;

    public Long getScreeningId() { return screeningId; }
    public void setScreeningId(Long screeningId) { this.screeningId = screeningId; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }
    public LocalDate getScreeningDate() { return screeningDate; }
    public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
