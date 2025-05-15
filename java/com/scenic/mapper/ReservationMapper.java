package com.scenic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scenic.entity.Reservation;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    @Insert("INSERT INTO reservation (user_id, scenic_spot_id, reservation_date, time_slot, " +
            "visitor_count, total_price, actual_price, status, create_time, update_time) " +
            "VALUES (#{userId}, #{scenicSpotId}, #{reservationDate}, #{timeSlot}, " +
            "#{visitorCount}, #{totalPrice}, #{actualPrice}, #{status}, #{createTime}, #{updateTime})")
    int insert(Reservation reservation);

    @Select("SELECT * FROM reservation WHERE id = #{id}")
    Reservation selectById(@Param("id") Long id);

    @Update("UPDATE reservation SET status = #{status}, update_time = #{updateTime}, " +
            "total_price = #{totalPrice}, actual_price = #{actualPrice}, " +
            "visitor_count = #{visitorCount}, time_slot = #{timeSlot}, " +
            "reservation_date = #{reservationDate} " +
            "WHERE id = #{id}")
    int updateReservation(Reservation reservation);

    @Select("SELECT * FROM reservation ORDER BY create_time DESC")
    List<Reservation> selectList();
    
    @Select("SELECT COUNT(*) FROM reservation WHERE scenic_spot_id = #{scenicSpotId} " +
            "AND reservation_date = #{reservationDate} AND time_slot = #{timeSlot} " +
            "AND status IN (0, 1)")
    int getBookedCount(
        @Param("scenicSpotId") Long scenicSpotId,
        @Param("reservationDate") LocalDate reservationDate,
        @Param("timeSlot") String timeSlot
    );

    @Delete("DELETE FROM reservation WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM reservation WHERE user_id = #{userId} AND status = #{status}")
    int deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM reservation WHERE status = #{status}")
    List<Reservation> selectByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM reservation WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    long countBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(actual_price), 0) FROM reservation WHERE status = 1 AND update_time BETWEEN #{startTime} AND #{endTime}")
    double sumIncomeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(actual_price), 0) FROM reservation WHERE status = 1 AND update_time >= DATE_FORMAT(NOW(), '%Y-%m-01')")
    double calculateMonthlyIncome();

    @Select("SELECT id, user_id, scenic_spot_id, reservation_date, time_slot, " +
            "visitor_count, total_price, actual_price, status, create_time, update_time " +
            "FROM reservation " +
            "WHERE (#{keyword} IS NULL OR " +
            "EXISTS (SELECT 1 FROM scenic_spot s WHERE s.id = scenic_spot_id AND s.name LIKE CONCAT('%', #{keyword}, '%')) OR " +
            "EXISTS (SELECT 1 FROM sys_user u WHERE u.id = user_id AND u.username LIKE CONCAT('%', #{keyword}, '%'))) " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND (#{date} IS NULL OR DATE(reservation_date) = #{date}) " +
            "ORDER BY create_time DESC")
    Page<Reservation> selectPage(Page<Reservation> page, 
        @Param("keyword") String keyword,
        @Param("status") Integer status,
        @Param("date") LocalDate date);

    List<Reservation> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    List<Reservation> selectByUserId(@Param("userId") Long userId);
} 