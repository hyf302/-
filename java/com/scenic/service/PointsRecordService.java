package com.scenic.service;

import com.scenic.entity.PointsRecord;
import java.util.List;

public interface PointsRecordService {
    // 获取用户积分记录
    List<PointsRecord> getUserPointsRecords(Long userId);
    
    // 创建积分记录
    PointsRecord createRecord(PointsRecord record);
    
    // 获取用户总积分
    int getUserTotalPoints(Long userId);

    List<PointsRecord> getPointsRecordsByUserId(Long userId);
    void addPointsRecord(Long userId, Integer points, String type, String description);
} 
 