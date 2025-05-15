package com.scenic.service.impl;

import com.scenic.entity.Admin;
import com.scenic.entity.User;
import com.scenic.exception.BusinessException;
import com.scenic.mapper.UserMapper;
import com.scenic.service.AdminService;
import com.scenic.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.StringUtils;
import com.scenic.util.MemberLevelUtil;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, AdminService adminService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先尝试从管理员表查找
        Admin admin = adminService.getAdminByUsername(username);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        
        // 如果不是管理员，则从用户表查找
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 设置默认值
        user.setMemberLevel(0);  // 设置默认会员等级为0（普通会员）
        user.setPoints(0);       // 设置默认积分为0
        user.setStatus(1);       // 设置状态为正常
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户
        if (userMapper.insert(user) > 0) {
            return user;
        }
        throw new RuntimeException("注册失败");
    }

    @Override
    public User login(String username, String password) {
        try {
            log.info("Attempting to find user: {}", username);
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                log.warn("User not found: {}", username);
                throw new RuntimeException("用户名或密码错误");
            }
            
            log.info("Checking password for user: {}, stored hash: {}", username, user.getPassword());
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Password mismatch for user: {}, input: {}, stored: {}", 
                    username, password, user.getPassword());
                throw new RuntimeException("用户名或密码错误");
            }

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.update(user);

            // 清除密码等敏感信息
            user.setPassword(null);
            return user;
            
        } catch (Exception e) {
            log.error("Login failed for user: {}, error: {}", username, e.getMessage(), e);
            throw new RuntimeException("登录失败，请稍后重试");
        }
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        
        // 保留敏感字段和积分相关字段
        user.setPassword(existingUser.getPassword());
        user.setUsername(existingUser.getUsername());
        user.setCreateTime(existingUser.getCreateTime());
        user.setPoints(existingUser.getPoints());
        user.setMemberLevel(MemberLevelUtil.calculateLevel(existingUser.getPoints())); // 根据积分计算等级
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新用户信息
        if (userMapper.updateById(user) > 0) {
            return userMapper.selectById(user.getId());
        }
        throw new RuntimeException("更新用户失败");
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
        return true;
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException("用户未登录");
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("无法获取当前用户信息");
        }
        return user.getId();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException("用户未登录");
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return getUserByUsername(username);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public long countTotalUsers() {
        return userMapper.countTotal();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return userMapper.searchUsers(keyword);
        }
        return userMapper.selectList(null);
    }

    @Override
    public void deleteUser(Long id) {
        if (userMapper.deleteById(id) <= 0) {
            throw new RuntimeException("删除用户失败");
        }
    }

    @Override
    public void updateUserStatus(Long id, int status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        
        if (userMapper.updateById(user) <= 0) {
            throw new RuntimeException("更新用户状态失败");
        }
    }

    @Override
    public void updateUserPoints(Long userId, int points) {
        User user = getUserById(userId);
        user.setPoints(points);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateMemberLevel(Long userId, int level) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setMemberLevel(level);
        user.setUpdateTime(LocalDateTime.now());
        
        if (userMapper.updateById(user) <= 0) {
            throw new RuntimeException("更新会员等级失败");
        }
        
        log.info("用户 {} 会员等级更新为 {}", userId, level);
    }
} 