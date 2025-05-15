package com.scenic.service;

import com.scenic.entity.MemberLevel;
import java.util.List;

public interface MemberService {
    /**
     * 获取用户当前会员等级
     */
    MemberLevel getCurrentLevel(int points);
    
    /**
     * 获取用户下一等级
     */
    MemberLevel getNextLevel(int currentLevel);
    
    /**
     * 获取所有会员等级配置
     */
    List<MemberLevel> getAllLevels();

    /**
     * 计算实际价格（考虑会员折扣）
     */
    double calculateActualPrice(Long userId, double originalPrice);

    /**
     * 更新用户积分
     */
    void updatePoints(Long userId, int pointsChange, String reason);

    /**
     * 获取距离下一等级所需积分
     */
    int getPointsToNextLevel(Long userId);

    void recordLevelChange(Long userId, int oldLevel, int newLevel, String reason);
} 
 