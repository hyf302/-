package com.scenic.service.impl;

import com.scenic.entity.Admin;
import com.scenic.mapper.AdminMapper;
import com.scenic.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Admin login(String username, String password) {
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            log.warn("Admin login failed: username {} not found", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 添加密码匹配的调试日志
        boolean matches = passwordEncoder.matches(password, admin.getPassword());
        log.info("Password match result for admin {}: {}", username, matches);
        log.debug("Raw password: {}, Encoded password in DB: {}", password, admin.getPassword());
        
        if (!matches) {
            log.warn("Admin login failed: password not match for username {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        log.info("Admin {} login successful", username);
        return admin;
    }

    @Override
    public Admin getAdminByUsername(String username) {
        return adminMapper.selectByUsername(username);
    }
} 