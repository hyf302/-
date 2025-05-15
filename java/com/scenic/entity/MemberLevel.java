package com.scenic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLevel {
    private Integer level;          // 等级编号
    private String name;           // 等级名称
    private Integer requiredPoints; // 所需积分
    private Double discount;       // 折扣率
    private String privileges;     // 特权说明
} 
 