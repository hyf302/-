package com.scenic.service;

import com.scenic.entity.ScenicSpot;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ScenicSpotService extends IService<ScenicSpot> {
    List<ScenicSpot> getAllScenicSpots();
    ScenicSpot getScenicSpotById(Long id);
    boolean addScenicSpot(ScenicSpot scenicSpot);
    boolean updateScenicSpot(ScenicSpot scenicSpot);
    boolean deleteScenicSpot(Long id);
    long countTotalScenicSpots();
    
    // 保留这个搜索方法
    List<ScenicSpot> search(String keyword);
} 