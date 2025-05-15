package com.scenic.controller;

import com.scenic.service.ReviewService;
import com.scenic.common.Result;
import com.scenic.entity.Review;
import com.scenic.vo.ReviewVO;
import com.scenic.entity.User;
import com.scenic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/my")
    public Result<List<ReviewVO>> getMyReviews() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.error("用户未登录");
        }
        List<ReviewVO> reviews = reviewService.getUserReviews(user.getId());
        return Result.success(reviews);
    }

    @GetMapping("/reservation/{reservationId}")
    public Result<Review> getReservationReview(@PathVariable Long reservationId) {
        Review review = reviewService.getReservationReview(reservationId);
        return Result.success(review);
    }

    @GetMapping("/scenic-spot/{scenicSpotId}")
    public Result<List<ReviewVO>> getScenicSpotReviews(@PathVariable Long scenicSpotId) {
        List<ReviewVO> reviews = reviewService.getScenicSpotReviews(scenicSpotId);
        return Result.success(reviews);
    }
} 
 