package com.scenic.controller;

import com.scenic.entity.ScenicSpot;
import com.scenic.service.ScenicSpotService;
import com.scenic.service.UserService;
import com.scenic.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.scenic.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import com.scenic.entity.Announcement;
import com.scenic.service.AnnouncementService;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final UserService userService;
    private final ScenicSpotService scenicSpotService;
    private final MemberService memberService;
    private final AnnouncementService announcementService;
    private static final Logger log = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // 获取最新的3条已发布公告
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)
                    .orderByDesc(Announcement::getCreateTime)
                    .last("LIMIT 3");
        List<Announcement> announcements = announcementService.list(queryWrapper);
        model.addAttribute("announcements", announcements);
        
        if (request.getUserPrincipal() != null) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", request.getUserPrincipal().getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/";
        }
        return "register";
    }

    @GetMapping("/my-reservations")
    public String myReservations(HttpSession session, Model model) {
        try {
            // 获取当前用户并存入session
            User currentUser = userService.getCurrentUser();
            session.setAttribute("user", currentUser);
            model.addAttribute("user", currentUser);
            return "my-reservations";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/announcements")
    public String announcements(Model model) {
        // 只查询已发布的公告
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)  // 状态为1表示已发布
                    .orderByDesc(Announcement::getCreateTime);  // 按创建时间倒序排序
        
        List<Announcement> announcements = announcementService.list(queryWrapper);
        model.addAttribute("announcements", announcements);
        return "announcements";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        try {
            if (userService.isAuthenticated()) {
                model.addAttribute("currentUser", userService.getCurrentUser());
            }
        } catch (Exception e) {
            // 处理异常
        }
    }
} 