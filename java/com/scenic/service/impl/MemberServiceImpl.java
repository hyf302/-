package com.scenic.service.impl;

import com.scenic.entity.MemberLevel;
import com.scenic.entity.User;
import com.scenic.mapper.MemberLevelMapper;
import com.scenic.service.MemberService;
import com.scenic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    
    private final MemberLevelMapper memberLevelMapper;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    
    private final List<MemberLevel> levels = Arrays.asList(
        new MemberLevel(0, "普通会员", 0, 1.0, "基础预约服务"),
        new MemberLevel(1, "银卡会员", 1000, 0.95, "95折优惠 优先预约服务"),
        new MemberLevel(2, "金卡会员", 5000, 0.90, "9折优惠 优先预约服务 特殊活动优先参与"),
        new MemberLevel(3, "钻石会员", 10000, 0.85, "85折优惠 最优先预约服务 特殊活动优先参与 VIP专属客服")
    );
    
    @Override
    public MemberLevel getCurrentLevel(int points) {
        return levels.stream()
                .filter(level -> points >= level.getRequiredPoints())
                .reduce((first, second) -> second)
                .orElse(levels.get(0));
    }
    
    @Override
    public MemberLevel getNextLevel(int currentLevel) {
        if (currentLevel >= levels.size() - 1) {
            return null;
        }
        return levels.get(currentLevel + 1);
    }
    
    @Override
    public double calculateActualPrice(Long userId, double originalPrice) {
        User user = userService.getUserById(userId);
        MemberLevel level = getCurrentLevel(user.getPoints());
        return originalPrice * level.getDiscount();
    }
    
    @Override
    @Transactional
    public void updatePoints(Long userId, int pointsChange, String reason) {
        User user = userService.getUserById(userId);
        int newPoints = Math.max(0, user.getPoints() + pointsChange);
        user.setPoints(newPoints);
        
        // 更新用户积分
        userService.updateUserPoints(userId, newPoints);
        
        // 记录积分变更日志
        memberLevelMapper.insertPointsLog(userId, pointsChange, reason);
    }
    
    @Override
    public int getPointsToNextLevel(Long userId) {
        User user = userService.getUserById(userId);
        MemberLevel nextLevel = memberLevelMapper.getNextLevel(user.getPoints());
        if (nextLevel == null) {
            return 0; // 已是最高等级
        }
        return nextLevel.getRequiredPoints() - user.getPoints();
    }
    
    @Override
    public List<MemberLevel> getAllLevels() {
        return new ArrayList<>(levels);
    }
    
    @Override
    @Transactional
    public void recordLevelChange(Long userId, int oldLevel, int newLevel, String reason) {
        try {
            // 更新用户会员等级
            userService.updateMemberLevel(userId, newLevel);
            
            // 记录等级变更日志
            String levelNames = String.format("%s -> %s",
                getLevelName(oldLevel),
                getLevelName(newLevel)
            );
            
            log.info("用户 {} 会员等级变更: {}, 原因: {}", userId, levelNames, reason);
        } catch (Exception e) {
            log.error("会员等级变更失败", e);
            throw new RuntimeException("会员等级变更失败: " + e.getMessage());
        }
    }
    
    private String getLevelName(int level) {
        switch (level) {
            case 0: return "普通会员";
            case 1: return "银卡会员";
            case 2: return "金卡会员";
            case 3: return "钻石会员";
            default: return "未知等级";
        }
    }
} 
 