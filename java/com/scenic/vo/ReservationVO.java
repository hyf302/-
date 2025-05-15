package com.scenic.vo;

import com.scenic.entity.Reservation;
import com.scenic.entity.ScenicSpot;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationVO {
    // 从 Reservation 继承的字段
    private Long id;
    private Long userId;
    private Long scenicSpotId;
    private String username;        // 用户名
    private String scenicSpotName; // 景点名称
    private LocalDate reservationDate;
    private String timeSlot;
    private Integer visitorCount;
    private BigDecimal totalPrice;
    private BigDecimal actualPrice;
    private Integer status;
    private String statusDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean hasReview;
    
    // 从 Reservation 转换为 VO
    public static ReservationVO fromReservation(Reservation reservation) {
        ReservationVO vo = new ReservationVO();
        vo.setId(reservation.getId());
        vo.setUserId(reservation.getUserId());
        vo.setScenicSpotId(reservation.getScenicSpotId());
        vo.setReservationDate(reservation.getReservationDate());
        vo.setTimeSlot(reservation.getTimeSlot());
        vo.setVisitorCount(reservation.getVisitorCount());
        vo.setTotalPrice(reservation.getTotalPrice());
        vo.setActualPrice(reservation.getActualPrice());
        vo.setStatus(reservation.getStatus());
        vo.setCreateTime(reservation.getCreateTime());
        vo.setUpdateTime(reservation.getUpdateTime());
        return vo;
    }
} 