package com.example.end_of_term.entity;

public class Seat {
    private Long seatId;
    private Long hallId;
    private Integer rowNum;
    private Integer colNum;
    private String seatCode;
    private Integer seatType;
    private Integer status;

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }
    public Integer getRowNum() { return rowNum; }
    public void setRowNum(Integer rowNum) { this.rowNum = rowNum; }
    public Integer getColNum() { return colNum; }
    public void setColNum(Integer colNum) { this.colNum = colNum; }
    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }
    public Integer getSeatType() { return seatType; }
    public void setSeatType(Integer seatType) { this.seatType = seatType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
