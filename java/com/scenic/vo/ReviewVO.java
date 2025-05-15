package com.scenic.vo;

import com.scenic.entity.Review;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewVO extends Review {
    private String userName;       // 评价用户名
    private String scenicSpotName; // 景点名称
} 
 