package com.scenic.controller;

import com.scenic.entity.MemberLevel;
import com.scenic.entity.PointsRecord;
import com.scenic.entity.User;
import com.scenic.service.MemberService;
import com.scenic.service.UserService;
import com.scenic.service.PointsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    private final UserService userService;
    private final PointsRecordService pointsRecordService;
    
    @GetMapping("/profile")
    public String showProfile(Model model) {
        try {
            // 获取当前登录用户
            User user = userService.getCurrentUser();
            model.addAttribute("user", user);

            // 获取会员等级信息
            MemberLevel currentLevel = memberService.getCurrentLevel(user.getPoints());
            model.addAttribute("currentLevel", currentLevel);
            
            // 获取下一等级信息
            MemberLevel nextLevel = memberService.getNextLevel(currentLevel.getLevel());
            if (nextLevel != null) {
                int pointsToNext = nextLevel.getRequiredPoints() - user.getPoints();
                model.addAttribute("nextLevel", nextLevel);
                model.addAttribute("pointsToNext", pointsToNext > 0 ? pointsToNext : 0);
            }

            // 获取积分记录
            List<PointsRecord> pointsRecords = pointsRecordService.getUserPointsRecords(user.getId());
            model.addAttribute("pointsRecords", pointsRecords);

            return "member/profile";
        } catch (Exception e) {
            log.error("获取会员信息失败", e);
            model.addAttribute("error", "获取会员信息失败：" + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/points")
    public String showPoints(Model model) {
        try {
            User user = userService.getCurrentUser();
            List<PointsRecord> pointsRecords = pointsRecordService.getUserPointsRecords(user.getId());
            model.addAttribute("pointsRecords", pointsRecords);
            return "member/points";
        } catch (Exception e) {
            log.error("获取积分记录失败", e);
            model.addAttribute("error", "获取积分记录失败：" + e.getMessage());
            return "error";
        }
    }
} 
 