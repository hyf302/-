package com.scenic.service.impl;

import com.scenic.entity.ScenicSpot;
import com.scenic.mapper.ScenicSpotMapper;
import com.scenic.service.ScenicSpotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScenicSpotServiceImpl extends ServiceImpl<ScenicSpotMapper, ScenicSpot> implements ScenicSpotService {
    private static final Logger log = LoggerFactory.getLogger(ScenicSpotServiceImpl.class);

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    private static final long STATUS_UPDATE_INTERVAL = 5 * 60 * 1000; // 5分钟更新一次状态

    @Cacheable(value = "scenicSpots", key = "'all'")
    @Override
    public List<ScenicSpot> getAllScenicSpots() {
        List<ScenicSpot> spots = scenicSpotMapper.selectList(null);
        LocalDateTime now = LocalDateTime.now();
        
        // 批量更新需要更新的景点
        List<ScenicSpot> needUpdateSpots = spots.stream()
            .filter(spot -> needStatusUpdate(spot, now))
            .collect(Collectors.toList());
            
        if (!needUpdateSpots.isEmpty()) {
            needUpdateSpots.forEach(spot -> updateSpotStatus(spot, now));
            this.updateBatchById(needUpdateSpots);
        }
        
        return spots;
    }

    @Cacheable(value = "scenicSpots", key = "#id")
    @Override
    public ScenicSpot getScenicSpotById(Long id) {
        return scenicSpotMapper.selectById(id);
    }

    @CacheEvict(value = "scenicSpots", allEntries = true)
    @Override
    public boolean addScenicSpot(ScenicSpot scenicSpot) {
        scenicSpot.setCreateTime(LocalDateTime.now());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        return scenicSpotMapper.insert(scenicSpot) > 0;
    }

    @CacheEvict(value = "scenicSpots", allEntries = true)
    @Override
    public boolean updateScenicSpot(ScenicSpot scenicSpot) {
        scenicSpot.setUpdateTime(LocalDateTime.now());
        return scenicSpotMapper.updateById(scenicSpot) > 0;
    }

    @Override
    public boolean deleteScenicSpot(Long id) {
        return scenicSpotMapper.deleteById(id) > 0;
    }

    @Override
    public long countTotalScenicSpots() {
        return scenicSpotMapper.countTotal();
    }

    @Override
    @Cacheable(value = "scenicSpots", key = "'search_' + #keyword")
    public List<ScenicSpot> search(String keyword) {
        List<ScenicSpot> spots;
        if (StringUtils.isEmpty(keyword)) {
            spots = scenicSpotMapper.selectList(null);
        } else {
            spots = scenicSpotMapper.search(keyword);
        }
        // 只在必要时更新状态
        LocalDateTime now = LocalDateTime.now();
        spots.stream()
            .filter(spot -> needStatusUpdate(spot, now))
            .forEach(spot -> updateSpotStatus(spot, now));
        return spots;
    }

    private boolean needStatusUpdate(ScenicSpot spot, LocalDateTime now) {
        if (spot.getUpdateTime() == null) return true;
        return spot.getUpdateTime().plusMinutes(5).isBefore(now);
    }

    private void updateSpotStatus(ScenicSpot spot, LocalDateTime now) {
        try {
            LocalTime currentTime = now.toLocalTime();
            LocalTime openTime = LocalTime.parse(spot.getOpenTime());
            LocalTime closeTime = LocalTime.parse(spot.getCloseTime());
            
            boolean isOpen;
            if (closeTime.isAfter(openTime)) {
                isOpen = !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
            } else {
                isOpen = !currentTime.isBefore(openTime) || !currentTime.isAfter(closeTime);
            }
            
            spot.setStatus(isOpen ? 1 : 0);
            spot.setUpdateTime(now);
            
        } catch (Exception e) {
            log.error("更新景点[{}]状态失败: {}", spot.getName(), e.getMessage());
        }
    }
} 