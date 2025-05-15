package com.scenic.controller;

import com.scenic.common.Result;
import com.scenic.entity.User;
import com.scenic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return Result.success(userService.register(user));
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());
        try {
            // 创建认证令牌
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            
            // 进行认证
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // 设置认证信息到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 返回用户信息
            User user = userService.login(request.getUsername(), request.getPassword());
            log.info("Login successful for user: {}", request.getUsername());
            return Result.success(user);
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PutMapping
    public Result<User> updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestParam Long userId,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        return Result.success(userService.changePassword(userId, oldPassword, newPassword));
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 