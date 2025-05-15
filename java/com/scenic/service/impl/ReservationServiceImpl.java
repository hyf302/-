package com.scenic.service.impl;

import com.scenic.entity.Reservation;
import com.scenic.entity.ScenicSpot;
import com.scenic.entity.Review;
import com.scenic.entity.User;
import com.scenic.exception.BusinessException;
import com.scenic.mapper.ReservationMapper;
import com.scenic.mapper.ScenicSpotMapper;
import com.scenic.service.ReservationService;
import com.scenic.service.ScenicSpotService;
import com.scenic.service.UserService;
import com.scenic.service.MemberService;
import com.scenic.service.ReviewService;
import com.scenic.service.PointsRecordService;
import com.scenic.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;
import com.scenic.entity.ReservationStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scenic.dto.ReservationSearchDTO;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationMapper reservationMapper;
    private final ScenicSpotMapper scenicSpotMapper;
    private final UserService userService;
    private final ScenicSpotService scenicSpotService;
    private final MemberService memberService;
    private final ReviewService reviewService;
    private final PointsRecordService pointsRecordService;
    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // 检查景点是否存在
        ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
        if (scenicSpot == null) {
            throw new BusinessException("景点不存在");
        }

        // 验证预约日期
        if (reservation.getReservationDate().isBefore(LocalDate.now())) {
            throw new BusinessException("不能预约过去的日期");
        }

        // 验证预约人数
        if (reservation.getVisitorCount() <= 0) {
            throw new BusinessException("预约人数必须大于0");
        }

        // 检查该时间段是否还有余量
        checkCapacity(scenicSpot, reservation);

        // 计算原始价格
        BigDecimal originalPrice = scenicSpot.getPrice().multiply(BigDecimal.valueOf(reservation.getVisitorCount()));
        reservation.setTotalPrice(originalPrice);
        
        // 计算会员折扣价格
        double discountedPrice = memberService.calculateActualPrice(reservation.getUserId(), originalPrice.doubleValue());
        reservation.setActualPrice(BigDecimal.valueOf(discountedPrice));
        
        reservation.setStatus(Reservation.STATUS_PENDING_PAYMENT);
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setUpdateTime(LocalDateTime.now());

        // 创建预约
        reservationMapper.insert(reservation);
        
        // 处理积分奖励
        handleReservationSuccess(reservation.getId());
        
        return reservation;
    }

    private void checkCapacity(ScenicSpot scenicSpot, Reservation reservation) {
        // 获取当天该时间段的预约总人数
        int bookedCount = reservationMapper.getBookedCount(
            reservation.getScenicSpotId(),
            reservation.getReservationDate(),
            reservation.getTimeSlot()
        );
        
        if (bookedCount + reservation.getVisitorCount() > scenicSpot.getMaxCapacity()) {
            throw new BusinessException("该时间段预约人数已满");
        }
    }

    @Override
    public List<Reservation> getUserReservations(Long userId) {
        return reservationMapper.selectByUserId(userId);
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationMapper.selectById(id);
    }

    @Override
    public List<ReservationVO> getMyReservations(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        // 获取用户的所有预约记录
        List<Reservation> reservations = reservationMapper.selectByUserId(userId);
        
        return reservations.stream().map(reservation -> {
            ReservationVO vo = ReservationVO.fromReservation(reservation);
            
            // 获取景点信息
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
            vo.setScenicSpotName(scenicSpot != null ? scenicSpot.getName() : "已删除的景点");
            
            // 检查是否已评价
            Review review = reviewService.getReservationReview(reservation.getId());
            vo.setHasReview(review != null);
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 创建一个表示已删除景点的对象
     */
    private ScenicSpot createDeletedSpot(Long id) {
        ScenicSpot spot = new ScenicSpot();
        spot.setId(id);
        spot.setName("已删除的景点");
        spot.setDescription("该景点信息已不存在");
        // 设置其他必要的默认值
        spot.setStatus(0);
        spot.setPrice(BigDecimal.ZERO);
        return spot;
    }

    @Override
    @Transactional
    public void deleteReservation(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此预约");
        }
        reservationMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void clearUserReservations(Long userId) {
        reservationMapper.deleteByUserIdAndStatus(userId, 3); // 删除已取消的预约
    }

    @Override
    public int deleteByUserIdAndStatus(Long userId, Integer status) {
        log.info("Deleting reservations for userId: {} with status: {}", userId, status);
        try {
            // 直接返回 mapper 的执行结果（受影响的行数）
            int result = reservationMapper.deleteByUserIdAndStatus(userId, status);
            log.info("Deleted {} reservations", result);
            return result;
        } catch (Exception e) {
            log.error("Error deleting reservations", e);
            throw new RuntimeException("删除预约记录失败", e);
        }
    }

    @Override
    public double calculateActualPrice(Reservation reservation) {
        ScenicSpot spot = scenicSpotMapper.selectById(reservation.getScenicSpotId());
        BigDecimal originalPrice = spot.getPrice().multiply(BigDecimal.valueOf(reservation.getVisitorCount()));
        return memberService.calculateActualPrice(reservation.getUserId(), originalPrice.doubleValue());
    }

    @Override
    @Transactional
    public void handleReservationSuccess(Long reservationId) {
        // 删除这个方法，因为我们只在支付成功时奖励积分
    }

    @Override
    @Transactional
    public void handleReservationCancel(Long reservationId) {
        // 删除这个方法，因为我们只在退款时扣除积分
    }

    @Override
    @Transactional
    public void payReservation(Long reservationId, Long userId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约订单不存在");
        }
        
        // 验证用户权限
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        if (reservation.getStatus() != Reservation.STATUS_PENDING_PAYMENT) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 更新预约状态为已支付
        reservation.setStatus(Reservation.STATUS_PAID);
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.updateReservation(reservation);
        
        // 计算积分 (实付金额1:1积分)
        int pointsToAdd = reservation.getActualPrice().intValue();
        
        // 更新用户积分
        User user = userService.getUserById(userId);
        int newPoints = user.getPoints() + pointsToAdd;
        userService.updateUserPoints(userId, newPoints);
        
        // 记录积分变更
        pointsRecordService.addPointsRecord(
            userId,
            pointsToAdd,
            "RESERVATION_PAYMENT",
            String.format("预约支付成功奖励：预约ID-%d", reservationId)
        );
        
        log.info("订单支付成功 - 订单ID: {}, 用户ID: {}, 实付金额: {}, 获得积分: {}", 
            reservationId, userId, reservation.getActualPrice(), pointsToAdd);
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation != null && reservation.getStatus() == Reservation.STATUS_PENDING_PAYMENT) {
            reservation.setStatus(Reservation.STATUS_PAID);
            reservation.setUpdateTime(LocalDateTime.now());
            reservationMapper.updateReservation(reservation);
        }
    }

    @Override
    @Transactional
    public void refundReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        if (reservation.getStatus() != 1) {
            throw new BusinessException("该预约不是已支付状态，无法退款");
        }
        
        // 检查是否可以退款（例如：预约日期前一天可以退款）
        LocalDate today = LocalDate.now();
        if (reservation.getReservationDate().isBefore(today)) {
            throw new BusinessException("预约日期已过，无法退款");
        }
        
        // 更新预约状态为已退款
        reservation.setStatus(Reservation.STATUS_REFUNDED);
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.updateReservation(reservation);
        
        // 扣除之前奖励的积分
        int pointsToDeduct = -reservation.getActualPrice().intValue();
        memberService.updatePoints(
            userId,
            pointsToDeduct,
            "预约退款扣除积分：预约ID-" + reservationId
        );
    }

    @Override
    public void validateReservation(Reservation reservation) {
        // 检查预约日期是否合法
        if (reservation.getReservationDate().isBefore(LocalDate.now())) {
            throw new BusinessException("不能预约过去的日期");
        }
        
        // 获取该时段已预约人数
        int bookedCount = reservationMapper.getBookedCount(
            reservation.getScenicSpotId(),
            reservation.getReservationDate(),
            reservation.getTimeSlot()
        );
        
        // 假设每个时段最多允许100人预约
        final int MAX_VISITORS_PER_SLOT = 100;
        if (bookedCount + reservation.getVisitorCount() > MAX_VISITORS_PER_SLOT) {
            throw new BusinessException("该时段预约人数已满，请选择其他时段");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    @Transactional
    public void updateReservationStatus() {
        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationMapper.selectByStatus(Reservation.STATUS_PAID);
        
        for (Reservation reservation : reservations) {
            if (reservation.getReservationDate().isBefore(today) || 
                reservation.getReservationDate().isEqual(today)) {
                reservation.setStatus(Reservation.STATUS_COMPLETED);
                reservation.setUpdateTime(LocalDateTime.now());
                reservationMapper.updateReservation(reservation);
            }
        }
    }

    @Override
    public long countReservationsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return reservationMapper.countBetween(startTime, endTime);
    }
    
    @Override
    public double calculateIncomeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return reservationMapper.sumIncomeBetween(startTime, endTime);
    }

    @Override
    public double calculateMonthlyIncome() {
        return reservationMapper.calculateMonthlyIncome();
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationMapper.selectList(new QueryWrapper<>());
    }

    @Override
    @Transactional
    public void cancelReservation(Long id, Long userId) {
        Reservation reservation = getReservationById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 验证用户权限
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        // 只能取消待支付或已支付的预约
        if (reservation.getStatus() != Reservation.STATUS_PENDING_PAYMENT 
            && reservation.getStatus() != Reservation.STATUS_PAID) {
            throw new BusinessException("该预约状态无法取消");
        }
        
        // 更新预约状态为已取消
        reservation.setStatus(3); // 3 表示已取消
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        
        log.info("预约已取消 - 预约ID: {}, 用户ID: {}", id, userId);
    }

    @Override
    public boolean updateReservation(Reservation reservation) {
        try {
            // 更新预约状态和时间
            reservation.setUpdateTime(LocalDateTime.now());
            return reservationMapper.updateById(reservation) > 0;
        } catch (Exception e) {
            log.error("更新预约失败: {}", e.getMessage());
            throw new BusinessException("更新预约失败");
        }
    }

    @Override
    @Transactional
    public void adminCancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 管理员可以取消任何状态的预约
        reservation.setStatus(3); // 3 表示已取消
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        
        log.info("管理员取消预约 - 预约ID: {}", id);
    }
    @Override
    public Page<ReservationVO> searchReservations(String keyword, String status, LocalDateTime startDate, LocalDateTime endDate, Page<Reservation> page) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        
        // 添加关键字搜索条件（通过关联查询）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .exists("SELECT 1 FROM scenic_spot s WHERE s.id = scenic_spot_id AND s.name LIKE CONCAT('%',{0},'%')", keyword)
                .or()
                .exists("SELECT 1 FROM sys_user u WHERE u.id = user_id AND u.username LIKE CONCAT('%',{0},'%')", keyword)
            );
        }
        
        // 添加状态搜索条件
        if (StringUtils.hasText(status)) {
            wrapper.eq(Reservation::getStatus, Integer.parseInt(status));
        }
        
        // 添加日期范围搜索条件
        if (startDate != null && endDate != null) {
            wrapper.between(Reservation::getCreateTime, startDate, endDate);
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(Reservation::getCreateTime);
        
        try {
            // 执行分页查询
            Page<Reservation> result = reservationMapper.selectPage(page, wrapper);
            
            // 转换为VO对象
            List<ReservationVO> records = result.getRecords().stream().map(reservation -> {
                ReservationVO vo = new ReservationVO();
                BeanUtils.copyProperties(reservation, vo);
                
                // 设置状态描述
                switch (reservation.getStatus()) {
                    case 0:
                        vo.setStatusDesc("待支付");
                        break;
                    case 1:
                        vo.setStatusDesc("已支付");
                        break;
                    case 2:
                        vo.setStatusDesc("已完成");
                        break;
                    case 3:
                        vo.setStatusDesc("已取消");
                        break;
                    default:
                        vo.setStatusDesc("未知状态");
                }
                
                // 获取用户信息
                User user = userService.getUserById(reservation.getUserId());
                if (user != null) {
                    vo.setUsername(user.getUsername());
                }
                
                // 获取景点信息
                ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
                if (scenicSpot != null) {
                    vo.setScenicSpotName(scenicSpot.getName());
                }
                
                return vo;
            }).collect(Collectors.toList());
            
            Page<ReservationVO> voPage = new Page<>();
            BeanUtils.copyProperties(result, voPage, "records");
            voPage.setRecords(records);
            return voPage;
        } catch (Exception e) {
            log.error("查询预约列表失败", e);
            throw new BusinessException("查询预约列表失败：" + e.getMessage());
        }
    }

    @Override
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        try {
            Reservation reservation = reservationMapper.selectById(reservationId);
            if (reservation == null) {
                throw new BusinessException("预约不存在");
            }
            
            // 验证状态转换
            ReservationStatus currentStatus = ReservationStatus.fromCode(reservation.getStatus());
            if (!isValidStatusTransition(currentStatus, status)) {
                throw new BusinessException("非法的状态变更操作");
            }
            
            reservation.setStatus(status.getCode());
            reservation.setUpdateTime(LocalDateTime.now());
            reservationMapper.updateById(reservation);
            
            log.info("Updated reservation {} status from {} to {}", 
                reservationId, currentStatus.getDescription(), status.getDescription());
        } catch (Exception e) {
            log.error("Error updating reservation status: ", e);
            throw new BusinessException("更新预约状态时发生错误", e);
        }
    }

    private boolean isValidStatusTransition(ReservationStatus currentStatus, ReservationStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == ReservationStatus.CONFIRMED 
                    || newStatus == ReservationStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == ReservationStatus.COMPLETED 
                    || newStatus == ReservationStatus.CANCELLED;
            case COMPLETED:
                return false; // 完成状态是终态
            case CANCELLED:
                return false; // 取消状态是终态
            default:
                return false;
        }
    }

    @Override
    public Page<Reservation> selectPage(Page<Reservation> page, String keyword, Integer status, LocalDate date) {
        return reservationMapper.selectPage(page, keyword, status, date);
    }

    @Override
    public List<ReservationVO> getReservationsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        List<Reservation> reservations = reservationMapper.selectByUserId(userId);
        return reservations.stream().map(reservation -> {
            ReservationVO vo = new ReservationVO();
            BeanUtils.copyProperties(reservation, vo);
            
            // 获取景点信息
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
            if (scenicSpot != null) {
                vo.setScenicSpotName(scenicSpot.getName());
            }
            
            // 获取用户信息
            User user = userService.getUserById(reservation.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            // 设置状态描述
            switch (reservation.getStatus()) {
                case 0: vo.setStatusDesc("待支付"); break;
                case 1: vo.setStatusDesc("已支付"); break;
                case 2: vo.setStatusDesc("已完成"); break;
                case 3: vo.setStatusDesc("已取消"); break;
                default: vo.setStatusDesc("未知状态");
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReservationVO> getReservationsByUserIdAndStatus(Long userId, Integer status) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        List<Reservation> reservations = reservationMapper.selectByUserIdAndStatus(userId, status);
        return reservations.stream().map(reservation -> {
            ReservationVO vo = new ReservationVO();
            BeanUtils.copyProperties(reservation, vo);
            
            // 获取景点信息
            ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
            if (scenicSpot != null) {
                vo.setScenicSpotName(scenicSpot.getName());
            }
            
            // 获取用户信息
            User user = userService.getUserById(reservation.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            // 设置状态描述
            switch (reservation.getStatus()) {
                case 0: vo.setStatusDesc("待支付"); break;
                case 1: vo.setStatusDesc("已支付"); break;
                case 2: vo.setStatusDesc("已完成"); break;
                case 3: vo.setStatusDesc("已取消"); break;
                default: vo.setStatusDesc("未知状态");
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
} 