package com.scenic.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer points;        // 积分变动值
    private String type;          // 变动类型：预约支付、退款、评价等
    private String description;    // 变动描述
    private Long relatedId;       // 关联ID（预约ID或评价ID）
    private LocalDateTime createTime;
} 
 