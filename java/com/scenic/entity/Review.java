package com.scenic.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long reservationId;
    private Long scenicSpotId;
    private Integer rating;        // 评分(1-5)
    private String content;        // 评价内容
    private String images;         // 图片URL，多个用逗号分隔
    private Integer status;        // 状态：0-待审核，1-已发布，2-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
 