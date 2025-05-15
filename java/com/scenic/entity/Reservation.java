package com.scenic.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private Long userId;
    private Long scenicSpotId;
    private LocalDate reservationDate;
    private String timeSlot;
    private Integer visitorCount;
    private BigDecimal totalPrice;    // 原价
    private BigDecimal actualPrice;   // 实付金额（考虑折扣后）
    
    // 预约状态常量
    public static final int STATUS_PENDING_PAYMENT = 0;    // 待支付
    public static final int STATUS_PAID = 1;              // 已支付
    public static final int STATUS_CANCELLED = 2;         // 已取消
    public static final int STATUS_REFUNDED = 3;          // 已退款
    public static final int STATUS_COMPLETED = 4;         // 已完成
    
    private Integer status;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 