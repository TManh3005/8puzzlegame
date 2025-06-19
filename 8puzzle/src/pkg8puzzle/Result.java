package com.example.npuzzleai;

public class Result {
    public String heuristic;
    public int approved;
    public int total;
    public long time;
    public int step;
    public String error;

    public Result(String heuristic, int approved, int total, int step, long time, String error) {
        this.heuristic = heuristic;
        this.approved = approved;
        this.total = total;
        this.step = step;
        this.time = time;
        this.error = error;
    }

    public String showResult(boolean showSolution) {
        StringBuilder rs = new StringBuilder("Thuật toán sử dụng Heuristic: " + this.heuristic + "\n");
        if (this.error == null) {
            rs.append("Số node đã duyệt: ").append(this.approved).append("\n")
              .append("Tổng số node trên cây: ").append(this.total).append("\n")
              .append("Số bước đi đến đích: ").append(this.step).append("\n")
              .append("Thời gian tìm kiếm: ").append(this.time).append("ms\n");
        } else {
            rs.append("Không tìm được lời giải\nNguyên nhân: ").append(this.error).append("\n");
        }
        return rs.toString();
    }
}