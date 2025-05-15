package com.scenic.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DashboardStats {
    private long totalUsers;        // 总用户数
    private long todayReservations; // 今日预约数
    private long totalScenicSpots;  // 景点总数
    private double monthlyIncome;   // 本月收入
} 