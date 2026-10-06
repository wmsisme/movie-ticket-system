package com.example.end_of_term.entity;

public class Hall {
    private Long hallId;
    private Long cinemaId;
    private String name;
    private Integer seatRows;
    private Integer seatCols;

    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }
    public Long getCinemaId() { return cinemaId; }
    public void setCinemaId(Long cinemaId) { this.cinemaId = cinemaId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSeatRows() { return seatRows; }
    public void setSeatRows(Integer seatRows) { this.seatRows = seatRows; }
    public Integer getSeatCols() { return seatCols; }
    public void setSeatCols(Integer seatCols) { this.seatCols = seatCols; }
}
