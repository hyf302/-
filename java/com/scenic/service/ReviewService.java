package com.scenic.service;

import com.scenic.entity.Review;
import com.scenic.vo.ReviewVO;
import java.util.List;

public interface ReviewService {
    // 创建评价
    Review createReview(Review review);
    
    // 获取景点的评价列表
    List<ReviewVO> getScenicSpotReviews(Long scenicSpotId);
    
    // 获取用户的评价列表
    List<ReviewVO> getUserReviews(Long userId);
    
    // 获取预约的评价
    Review getReservationReview(Long reservationId);
    
    // 删除评价
    void deleteReview(Long reviewId);
} 
 