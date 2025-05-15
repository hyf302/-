package com.scenic.service.impl;

import com.scenic.entity.PointsRecord;
import com.scenic.mapper.PointsRecordMapper;
import com.scenic.service.PointsRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsRecordServiceImpl implements PointsRecordService {
    private final PointsRecordMapper pointsRecordMapper;

    @Override
    public List<PointsRecord> getUserPointsRecords(Long userId) {
        return pointsRecordMapper.selectByUserId(userId);
    }

    @Override
    public List<PointsRecord> getPointsRecordsByUserId(Long userId) {
        return pointsRecordMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public PointsRecord createRecord(PointsRecord record) {
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insertRecord(record);
        return record;
    }

    @Override
    public int getUserTotalPoints(Long userId) {
        return pointsRecordMapper.sumPointsByUserId(userId);
    }

    @Override
    @Transactional
    public void addPointsRecord(Long userId, Integer points, String type, String description) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insertRecord(record);
    }
} 
 