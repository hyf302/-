package com.scenic.controller;

import com.scenic.common.Result;
import com.scenic.entity.ScenicSpot;
import com.scenic.vo.ReviewVO;
import com.scenic.entity.User;
import com.scenic.service.ScenicSpotService;
import com.scenic.service.UserService;
import com.scenic.service.MemberService;
import com.scenic.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/scenic-spots")
@RequiredArgsConstructor
public class ScenicSpotController {
    private final ScenicSpotService scenicSpotService;
    private final UserService userService;
    private final MemberService memberService;
    private final ReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ScenicSpotController.class);

    @GetMapping("/api/list")
    @ResponseBody
    public Result<List<ScenicSpot>> getAllScenicSpots() {
        List<ScenicSpot> spots = scenicSpotService.getAllScenicSpots();
        
        // 处理图片URL
        spots.forEach(spot -> {
            if (spot.getImageUrls() != null && !spot.getImageUrls().isEmpty()) {
                // 确保图片URL以正确的路径开头
                if (!spot.getImageUrls().startsWith("/")) {
                    spot.setImageUrls("/" + spot.getImageUrls());
                }
            } else {
                // 设置默认图片
                spot.setImageUrls("/images/default-scenic.jpg");
            }
        });
        
        return Result.success(spots);
    }

    @GetMapping("/{id}")
    public String getDetail(@PathVariable Long id, Model model) {
        ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(id);
        if (scenicSpot == null) {
            return "redirect:/404";
        }
        
        model.addAttribute("scenicSpot", scenicSpot);
        
        // 获取景点评价
        List<ReviewVO> reviews = reviewService.getScenicSpotReviews(id);
        double averageRating = reviews.stream()
            .mapToInt(ReviewVO::getRating)
            .average()
            .orElse(0.0);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("reviewCount", reviews.size());
        
        // 获取当前用户信息
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            // 用户未登录，忽略异常
        }
        
        // 设置认证状态和用户信息
        boolean isAuthenticated = currentUser != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if (isAuthenticated) {
            model.addAttribute("username", currentUser.getUsername());
            // 计算会员价格
            double discountedPrice = memberService.calculateActualPrice(
                currentUser.getId(), 
                scenicSpot.getPrice().doubleValue()
            );
            model.addAttribute("discountedPrice", discountedPrice);
        } else {
            model.addAttribute("username", null);
            model.addAttribute("discountedPrice", null);
        }
        
        return "scenic-spot-detail";
    }

    @GetMapping("/{id}/price")
    @ResponseBody
    public Result<Map<String, Object>> getPrice(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("用户未登录");
            }
            
            // 获取景点信息
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(id);
            if (scenicSpot == null) {
                return Result.error("景点不存在");
            }
            
            // 获取会员折扣价格
            double discountedPrice = memberService.calculateActualPrice(
                user.getId(), 
                scenicSpot.getPrice().doubleValue()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("originalPrice", scenicSpot.getPrice());
            result.put("discountedPrice", discountedPrice);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取价格信息失败", e);
            return Result.error("获取价格信息失败");
        }
    }

    @PostMapping
    @ResponseBody
    public Result<?> createScenicSpot(@RequestBody ScenicSpot scenicSpot) {
        if (scenicSpotService.addScenicSpot(scenicSpot)) {
            return Result.success(scenicSpot);
        }
        return Result.error("添加失败");
    }

    @PutMapping
    @ResponseBody
    public Result<?> updateScenicSpot(@RequestBody ScenicSpot scenicSpot) {
        if (scenicSpotService.updateScenicSpot(scenicSpot)) {
            return Result.success(scenicSpot);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result<Boolean> deleteScenicSpot(@PathVariable Long id) {
        return Result.success(scenicSpotService.deleteScenicSpot(id));
    }
} 