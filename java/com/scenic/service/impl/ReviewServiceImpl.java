package com.scenic.service.impl;

import com.scenic.entity.Review;
import com.scenic.entity.ScenicSpot;
import com.scenic.entity.User;
import com.scenic.vo.ReviewVO;
import com.scenic.mapper.ReviewMapper;
import com.scenic.mapper.ScenicSpotMapper;
import com.scenic.mapper.UserMapper;
import com.scenic.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final ScenicSpotMapper scenicSpotMapper;

    @Override
    public Review getReservationReview(Long reservationId) {
        return reviewMapper.selectByReservationId(reservationId);
    }

    @Override
    public List<ReviewVO> getUserReviews(Long userId) {
        List<Review> reviews = reviewMapper.selectByUserId(userId);
        return reviews.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReviewVO> getScenicSpotReviews(Long scenicSpotId) {
        List<Review> reviews = reviewMapper.selectByScenicSpotId(scenicSpotId);
        return reviews.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private ReviewVO convertToVO(Review review) {
        if (review == null) {
            return null;
        }
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 设置用户名
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setUserName(user.getUsername());
        }
        
        // 设置景点名称
        ScenicSpot scenicSpot = scenicSpotMapper.selectById(review.getScenicSpotId());
        if (scenicSpot != null) {
            vo.setScenicSpotName(scenicSpot.getName());
        }
        
        return vo;
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        reviewMapper.insert(review);
        return review;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewMapper.deleteById(reviewId);
    }
} 
 