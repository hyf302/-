package com.scenic.service;

import com.scenic.entity.Reservation;
import com.scenic.entity.ReservationStatus;
import com.scenic.dto.ReservationSearchDTO;
import com.scenic.vo.ReservationVO;
import com.scenic.exception.BusinessException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ReservationService {
    // 创建预约
    Reservation createReservation(Reservation reservation);
    
    // 获取用户预约列表
    List<Reservation> getUserReservations(Long userId);
    
    // 根据ID获取预约
    Reservation getReservationById(Long id);
    
    // 获取我的预约列表（包含景点信息）
    List<ReservationVO> getMyReservations(Long userId);
    
    // 删除预约（带用户ID验证）
    void deleteReservation(Long id, Long userId);
    
    // 清除用户预约
    void clearUserReservations(Long userId);
    
    // 根据用户ID和状态删除预约
    int deleteByUserIdAndStatus(Long userId, Integer status);

    /**
     * 计算预约实际价格（考虑会员折扣）
     */
    double calculateActualPrice(Reservation reservation);

    /**
     * 处理预约成功后的积分奖励
     */
    void handleReservationSuccess(Long reservationId);

    /**
     * 处理预约取消后的积分扣除
     */
    void handleReservationCancel(Long reservationId);

    /**
     * 支付预约
     * @param reservationId 预约ID
     * @param userId 用户ID
     * @throws BusinessException 如果支付失败
     */
    void payReservation(Long reservationId, Long userId);

    /**
     * 验证预约信息
     * @param reservation 预约信息
     * @throws BusinessException 如果验证失败
     */
    void validateReservation(Reservation reservation);

    /**
     * 处理预约支付成功后的积分奖励
     */
    void handlePaymentSuccess(Long reservationId);

    /**
     * 退款
     * @param reservationId 预约ID
     * @param userId 用户ID
     * @throws BusinessException 如果退款失败
     */
    void refundReservation(Long reservationId, Long userId);

    long countReservationsBetween(LocalDateTime startTime, LocalDateTime endTime);
    double calculateIncomeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 计算当月收入
     * @return 当月收入金额
     */
    double calculateMonthlyIncome();

    List<Reservation> getAllReservations();

    /**
     * 管理员取消预约
     * @param id 预约ID
     * @throws BusinessException 如果取消失败
     */
    void adminCancelReservation(Long id);

    /**
     * 用户取消预约
     * @param id 预约ID
     * @param userId 用户ID
     * @throws BusinessException 如果取消失败
     */
    void cancelReservation(Long id, Long userId);

    /**
     * 更新预约信息
     * @param reservation 预约信息
     * @return 更新是否成功
     */
    boolean updateReservation(Reservation reservation);

    /**
     * 搜索预约信息
     * @param keyword 关键字
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 分页参数
     * @return 分页后的预约信息
     */
    IPage<ReservationVO> searchReservations(String keyword, String status, 
        LocalDateTime startDate, LocalDateTime endDate, Page<Reservation> page);

    void updateReservationStatus(Long reservationId, ReservationStatus status);

    /**
     * 分页查询预约信息
     * @param page 分页参数
     * @param keyword 关键字（用户名或景点名）
     * @param status 预约状态
     * @param date 预约日期
     * @return 分页结果
     */
    Page<Reservation> selectPage(Page<Reservation> page, String keyword, Integer status, LocalDate date);

    /**
     * 获取预约的真实状态
     * @param reservation 预约对象
     * @return 真实状态值
     */
    default String getReservationStatus(Reservation reservation) {
        switch (reservation.getStatus()) {
            case 0:
                return "待支付";
            case 1:
                return "已取消";
            case 2:
                return "已完成";
            default:
                return "未知状态";
        }
    }

    List<ReservationVO> getReservationsByUserIdAndStatus(Long userId, Integer status);
    
    List<ReservationVO> getReservationsByUserId(Long userId);
} 