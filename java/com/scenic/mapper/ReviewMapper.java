package com.scenic.mapper;

import com.scenic.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {
    void insert(Review review);
    Review selectById(@Param("id") Long id);
    Review selectByReservationId(@Param("reservationId") Long reservationId);
    List<Review> selectByUserId(@Param("userId") Long userId);
    List<Review> selectByScenicSpotId(@Param("scenicSpotId") Long scenicSpotId);
    void deleteById(@Param("id") Long id);
} 
 