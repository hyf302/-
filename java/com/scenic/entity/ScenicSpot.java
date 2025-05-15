package com.scenic.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

@Data
@TableName("scenic_spot")
public class ScenicSpot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String address;
    private String openTime;
    private String closeTime;
    private Integer maxCapacity;
    private BigDecimal price;
    private String imageUrls;
    private Integer status;  // 1: 开放, 0: 关闭
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public List<String> getImageUrlList() {
        if (StringUtils.isEmpty(imageUrls)) {
            return Collections.emptyList();
        }
        return Arrays.asList(imageUrls.split(","));
    }

    public void setImageUrlList(List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            this.imageUrls = String.join(",", urls);
        } else {
            this.imageUrls = null;
        }
    }
} 