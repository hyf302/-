package com.scenic.controller;

import com.scenic.common.Result;
import com.scenic.dto.DashboardStats;
import com.scenic.entity.Admin;
import com.scenic.entity.ScenicSpot;
import com.scenic.entity.User;
import com.scenic.entity.Reservation;
import com.scenic.service.AdminService;
import com.scenic.service.UserService;
import com.scenic.service.ReservationService;
import com.scenic.service.ScenicSpotService;
import com.scenic.service.MemberService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.BeanUtils;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.scenic.vo.ReservationVO;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.format.DateTimeParseException;
import org.springframework.format.annotation.DateTimeFormat;
import com.scenic.entity.Announcement;
import com.scenic.service.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private ScenicSpotService scenicSpotService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AnnouncementService announcementService;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.allowed-types}")
    private List<String> allowedTypes;

    @Value("${upload.max-size}")
    private String maxFileSize;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // 获取当前登录用户
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("username", auth.getName());
            
            // 获取统计数据
            DashboardStats stats = getDashboardStats();
            model.addAttribute("stats", stats);
            
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "加载数据时发生错误");
            return "admin/dashboard";
        }
    }

    private DashboardStats getDashboardStats() {
        // 获取今天的开始和结束时间
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime tomorrow = today.plusDays(1);
        
        // 获取本月的开始和结束时间
        LocalDateTime monthStart = today.withDayOfMonth(1);
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        
        return DashboardStats.builder()
                .totalUsers(userService.countTotalUsers())
                .todayReservations(reservationService.countReservationsBetween(today, tomorrow))
                .totalScenicSpots(scenicSpotService.countTotalScenicSpots())
                .monthlyIncome(reservationService.calculateIncomeBetween(monthStart, monthEnd))
                .build();
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Result<Admin> apiLogin(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            Admin admin = adminService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (admin != null) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    admin.getUsername(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                
                // 设置认证信息
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // 设置session
                session.setAttribute("admin", admin);
                
                // 返回成功结果，并包含重定向URL
                return Result.success(admin).addExtra("redirectUrl", "/admin/dashboard");
            }
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("Admin login error", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 获取 Security 上下文
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            // 清除认证信息
            context.setAuthentication(null);
        }
        // 使 session 失效
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 重定向到登录页
        return "redirect:/admin/login";
    }

    // 景点列表页面
    @GetMapping("/scenic-spots")
    public String scenicSpotList(Model model, @RequestParam(required = false) String keyword) {
        List<ScenicSpot> spots;
        if (StringUtils.isEmpty(keyword)) {
            spots = scenicSpotService.getAllScenicSpots();
        } else {
            spots = scenicSpotService.search(keyword);
        }
        model.addAttribute("scenicSpots", spots);
        model.addAttribute("keyword", keyword);
        return "admin/scenic-spots";
    }
    
    // 添加景点页面
    @GetMapping("/scenic-spots/add")
    public String addScenicSpotForm() {
        return "admin/scenic-spots/add";
    }
    
    // 编辑景点页面
    @GetMapping("/scenic-spots/edit/{id}")
    public String editScenicSpotForm(@PathVariable Long id, Model model) {
        ScenicSpot spot = scenicSpotService.getScenicSpotById(id);
        model.addAttribute("spot", spot);
        return "admin/scenic-spots/edit";
    }

    // 添加景点
    @PostMapping("/api/scenic-spots")
    @ResponseBody
    public Result<?> createScenicSpot(@RequestBody ScenicSpot scenicSpot) {
        scenicSpot.setCreateTime(LocalDateTime.now());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        boolean success = scenicSpotService.addScenicSpot(scenicSpot);
        return success ? Result.success() : Result.error("添加失败");
    }
    
    // 更新景点
    @PutMapping("/api/scenic-spots/{id}")
    @ResponseBody
    public Result<?> updateScenicSpot(@PathVariable Long id, @RequestBody ScenicSpot scenicSpot) {
        try {
            // 获取原有景点信息
            ScenicSpot existingSpot = scenicSpotService.getScenicSpotById(id);
            if (existingSpot == null) {
                return Result.error("景点不存在");
            }

            // 如果没有新的图片URL，保留原有的图片URL
            if (StringUtils.isEmpty(scenicSpot.getImageUrls())) {
                scenicSpot.setImageUrls(existingSpot.getImageUrls());
            }

            scenicSpot.setId(id);
            scenicSpot.setUpdateTime(LocalDateTime.now());
            boolean success = scenicSpotService.updateScenicSpot(scenicSpot);
            
            if (success) {
                // 清除可能的缓存
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新景点失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    // 删除景点
    @DeleteMapping("/api/scenic-spots/{id}")
    @ResponseBody
    public Result<?> deleteScenicSpot(@PathVariable Long id) {
        boolean success = scenicSpotService.deleteScenicSpot(id);
        return success ? Result.success() : Result.error("删除失败");
    }
    
    // 获取单个景点信息
    @GetMapping("/api/scenic-spots/{id}")
    @ResponseBody
    public Result<ScenicSpot> getScenicSpot(@PathVariable Long id) {
        ScenicSpot spot = scenicSpotService.getScenicSpotById(id);
        return Result.success(spot);
    }

    @PutMapping("/api/scenic-spots/{id}/status")
    @ResponseBody
    public Result<?> updateScenicSpotStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("状态参数错误");
        }
        
        // 获取当前景点信息
        ScenicSpot spot = scenicSpotService.getScenicSpotById(id);
        if (spot == null) {
            return Result.error("景点不存在");
        }
        
        // 更新状态和时间
        spot.setStatus(status);
        spot.setUpdateTime(LocalDateTime.now());
        
        boolean success = scenicSpotService.updateScenicSpot(spot);
        return success ? Result.success() : Result.error("更新状态失败");
    }

    @PostMapping("/api/upload")
    @ResponseBody
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> imageUrls = new ArrayList<>();
        
        try {
            for (MultipartFile file : files) {
                // 检查文件类型
                String contentType = file.getContentType();
                if (!allowedTypes.contains(contentType)) {
                    return Result.error("不支持的文件类型: " + contentType);
                }
                
                // 使用 Spring 的文件大小限制配置，不需要手动检查
                
                // 生成文件名：时间戳 + 随机数 + 原始扩展名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = System.currentTimeMillis() + "_" + 
                                UUID.randomUUID().toString().substring(0, 8) + extension;
                
                // 按年月日组织目录结构
                String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                File dateDir = new File(uploadPath + datePath);
                if (!dateDir.exists()) {
                    dateDir.mkdirs();
                }
                
                // 保存文件
                File dest = new File(dateDir, filename);
                file.transferTo(dest);
                
                // 生成访问URL
                String imageUrl = "/uploads/" + datePath + "/" + filename;
                imageUrls.add(imageUrl);
                
                log.info("文件上传成功: {}", imageUrl);
            }
            
            return Result.success(imageUrls);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public String userManagement(Model model, @RequestParam(required = false) String keyword) {
        try {
            List<User> users;
            if (StringUtils.hasText(keyword)) {
                users = userService.searchUsers(keyword);
            } else {
                users = userService.getAllUsers();
            }
            model.addAttribute("users", users);
            model.addAttribute("keyword", keyword);
            return "admin/users";
        } catch (Exception e) {
            log.error("Error in user management", e);
            model.addAttribute("error", "获取用户列表失败：" + e.getMessage());
            return "admin/users";
        }
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public Result getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/users/update")
    @ResponseBody
    public Result updateUser(@RequestBody User user) {
        try {
            log.info("Updating user: {}", user);
            
            // 获取原用户信息
            User oldUser = userService.getUserById(user.getId());
            if (oldUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            userService.updateUser(user);
            return Result.success();
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public Result deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    @GetMapping("/reservations")
    public String reservationsPage(Model model, 
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer status,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        
        Page<Reservation> pageRequest = new Page<>(page, size);
        Page<Reservation> reservationPage = reservationService.selectPage(pageRequest, keyword, status, date);
        
        // 转换为 VO 对象
        List<ReservationVO> voList = reservationPage.getRecords().stream()
            .map(reservation -> {
                ReservationVO vo = new ReservationVO();
                BeanUtils.copyProperties(reservation, vo);
                
                // 获取关联数据
                try {
                    User user = userService.getUserById(reservation.getUserId());
                    vo.setUsername(user != null ? user.getUsername() : "未知用户");
                    
                    ScenicSpot spot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
                    vo.setScenicSpotName(spot != null ? spot.getName() : "未知景点");
                } catch (Exception e) {
                    log.error("获取预约关联信息失败", e);
                }
                
                return vo;
            }).collect(Collectors.toList());
        
        model.addAttribute("reservations", voList);
        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        
        return "admin/reservations";
    }

    @GetMapping("/api/reservations/{id}")
    @ResponseBody
    public Result<ReservationVO> getReservationDetails(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        
        ReservationVO vo = ReservationVO.fromReservation(reservation);
        
        // 获取用户信息
        User user = userService.getUserById(reservation.getUserId());
        vo.setUsername(user != null ? user.getUsername() : "未知用户");
        
        // 获取景点信息
        ScenicSpot scenicSpot = scenicSpotService.getScenicSpotById(reservation.getScenicSpotId());
        vo.setScenicSpotName(scenicSpot != null ? scenicSpot.getName() : "已删除的景点");
        
        return Result.success(vo);
    }

    @PutMapping("/api/reservations/{id}/cancel")
    @ResponseBody
    public Result<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.adminCancelReservation(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("取消预约失败：" + e.getMessage());
        }
    }

    @GetMapping("/api/admin/reservations/search")
    public ResponseEntity<?> searchReservations(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            // 转换日期字符串为 LocalDateTime
            LocalDateTime start = StringUtils.hasText(startDate) ? 
                LocalDate.parse(startDate).atStartOfDay() : null;
            LocalDateTime end = StringUtils.hasText(endDate) ? 
                LocalDate.parse(endDate).atTime(23, 59, 59) : null;

            // 创建分页对象
            Page<Reservation> page = new Page<>(pageNum, pageSize);

            // 调用服务层进行搜索
            IPage<ReservationVO> result = reservationService.searchReservations(
                keyword, status, start, end, page);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("搜索预约记录失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("搜索预约记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/announcements")
    public String announcementPage(Model model, 
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer status) {
        // 创建查询条件
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加标题或内容的模糊搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Announcement::getTitle, keyword)
                       .or()
                       .like(Announcement::getContent, keyword);
        }
        
        // 添加状态筛选
        if (status != null) {
            queryWrapper.eq(Announcement::getStatus, status);
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(Announcement::getCreateTime);
        
        // 分页查询
        Page<Announcement> page = new Page<>(1, 10);
        page = announcementService.page(page, queryWrapper);
        
        // 添加到模型
        model.addAttribute("announcements", page.getRecords());
        model.addAttribute("currentPage", page.getCurrent());
        model.addAttribute("totalPages", page.getPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        
        return "admin/announcements";
    }

    @PostMapping("/api/announcements")
    @ResponseBody
    public Result<Announcement> saveAnnouncement(@RequestBody Announcement announcement) {
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        if (announcement.getId() == null) {
            // 新增公告
            announcement.setCreateTime(now);
        }
        announcement.setUpdateTime(now);
        
        // 保存公告
        boolean success = announcementService.saveOrUpdate(announcement);
        
        if (success) {
            return Result.success(announcement);
        } else {
            return Result.error("保存失败");
        }
    }

    // 获取公告详情
    @GetMapping("/api/announcements/{id}")
    @ResponseBody
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement != null) {
            return Result.success(announcement);
        }
        return Result.error("公告不存在");
    }

    // 更新公告
    @PutMapping("/api/announcements/{id}")
    @ResponseBody
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcement.setUpdateTime(LocalDateTime.now());
        boolean success = announcementService.updateById(announcement);
        if (success) {
            return Result.success(announcement);
        }
        return Result.error("更新失败");
    }

    // 删除公告
    @DeleteMapping("/api/announcements/{id}")
    @ResponseBody
    public Result<?> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.removeById(id);
        if (success) {
            return Result.success();
        }
        return Result.error("删除失败");
    }
} 