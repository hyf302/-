package com.scenic.controller;

import com.scenic.common.Result;
import com.scenic.entity.Reservation;
import com.scenic.entity.ScenicSpot;
import com.scenic.entity.User;
import com.scenic.entity.Review;
import com.scenic.dto.ReviewRequest;
import com.scenic.exception.BusinessException;
import com.scenic.service.MemberService;
import com.scenic.service.ReservationService;
import com.scenic.service.ReviewService;
import com.scenic.service.UserService;
import com.scenic.service.ScenicSpotService;
import com.scenic.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.scenic.utils.UserContext;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;
    private final MemberService memberService;
    private final ReviewService reviewService;
    private final ScenicSpotService scenicSpotService;

    @PostMapping
    @ResponseBody
    public Result<?> createReservation(@RequestBody Reservation reservation, Authentication authentication) {
        try {
            // 从Spring Security的Authentication对象中获取当前登录用户信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // 通过用户名获取用户ID并设置到预约对象中
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            reservation.setUserId(user.getId());
            
            // 创建预约
            reservationService.createReservation(reservation);
            return Result.success();
        } catch (BusinessException e) {
            log.error("创建预约失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建预约失败", e);
            return Result.error("创建预约失败");
        }
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public Result<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        return Result.success(reservationService.getUserReservations(userId));
    }

    @GetMapping("/my")
    @ResponseBody
    public Result getMyReservations(Integer status) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 通过用户名获取用户信息
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户未登录");
        }
        
        // 根据用户ID和状态获取预约记录
        List<ReservationVO> reservations;
        if (status != null) {
            reservations = reservationService.getReservationsByUserIdAndStatus(user.getId(), status);
        } else {
            reservations = reservationService.getReservationsByUserId(user.getId());
        }
        
        return Result.success(reservations);
    }

    @PostMapping("/{id}/cancel")
    @ResponseBody
    public Result<Boolean> cancelReservation(@PathVariable Long id) {
        try {
            // 获取当前用户
            User user = userService.getCurrentUser();
            if (user == null) {
                return Result.error("用户未登录");
            }

            // 调用取消预约服务，传入预约ID和用户ID
            reservationService.cancelReservation(id, user.getId());
            
            return Result.success(true);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result deleteReservation(@PathVariable Long id) {
        Long userId = userService.getCurrentUserId();
        try {
            reservationService.deleteReservation(id, userId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    @ResponseBody
    public Result clearReservations(@RequestParam(required = false) Integer status) {
        // 获取当前登录用户ID
        Long userId = userService.getCurrentUserId();
        
        // 如果status为null，默认清除已取消的预约(status=3)
        status = status == null ? 3 : status;
        
        try {
            int count = reservationService.deleteByUserIdAndStatus(userId, status);
            return Result.success("成功删除" + count + "条预约记录");
        } catch (Exception e) {
            log.error("Clear reservations failed", e);
            return Result.error("清除预约记录失败：" + e.getMessage());
        }
    }

    @GetMapping
    public String showReservationPage(Model model, HttpSession session) {
        try {
            // 获取当前用户
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userService.getUserByUsername(userDetails.getUsername());
            
            // 将用户信息存入session和model
            session.setAttribute("user", user);
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            
            return "reservation";
        } catch (Exception e) {
            log.error("加载预约页面失败", e);
            return "error";
        }
    }

    @PostMapping("/calculate-price")
    @ResponseBody
    public Result<Map<String, Object>> calculatePrice(@RequestBody Reservation reservation, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("用户未登录");
            }

            double originalPrice = reservation.getTotalPrice().doubleValue();
            double discountedPrice = memberService.calculateActualPrice(user.getId(), originalPrice);
            
            Map<String, Object> priceInfo = new HashMap<>();
            priceInfo.put("originalPrice", originalPrice);
            priceInfo.put("discountedPrice", discountedPrice);
            
            return Result.success(priceInfo);
        } catch (Exception e) {
            log.error("计算价格失败", e);
            return Result.error("计算价格失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    @ResponseBody
    public Result<Boolean> payReservation(@PathVariable Long id) {
        try {
            // 获取当前用户
            User user = userService.getCurrentUser();
            if (user == null) {
                return Result.error("用户未登录");
            }

            // 调用支付服务
            reservationService.payReservation(id, user.getId());
            
            return Result.success(true);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/refund")
    @ResponseBody
    public Result<Boolean> refundReservation(@PathVariable Long id) {
        try {
            // 获取当前用户
            User user = userService.getCurrentUser();
            if (user == null) {
                return Result.error("用户未登录");
            }

            // 调用退款服务
            reservationService.refundReservation(id, user.getId());
            
            return Result.success(true);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/review")
    @ResponseBody
    public Result<Boolean> submitReview(
        @PathVariable Long id,
        @RequestBody ReviewRequest reviewRequest
    ) {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                return Result.error("用户未登录");
            }

            // 获取预约信息
            Reservation reservation = reservationService.getReservationById(id);
            if (reservation == null) {
                return Result.error("预约不存在");
            }

            Review review = new Review();
            review.setUserId(user.getId());
            review.setReservationId(id);
            review.setScenicSpotId(reservation.getScenicSpotId());
            review.setRating(reviewRequest.getRating());
            review.setContent(reviewRequest.getContent());
            review.setStatus(1);  // 1-已发布

            reviewService.createReview(review);
            
            // 评价成功后奖励积分
            memberService.updatePoints(
                user.getId(),
                10,  // 评价奖励10积分
                "评价景点奖励积分：预约ID-" + id
            );
            
            return Result.success(true);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
} 