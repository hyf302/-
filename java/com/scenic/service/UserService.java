package com.scenic.service;

import com.scenic.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User register(User user);
    User login(String username, String password);
    User getUserById(Long id);
    User updateUser(User user);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    Long getCurrentUserId();
    boolean isAuthenticated();
    User getCurrentUser();
    User getUserByUsername(String username);
    long countTotalUsers();
    List<User> getAllUsers();
    List<User> searchUsers(String keyword);
    void deleteUser(Long id);
    void updateUserStatus(Long id, int status);
    /**
     * 更新用户积分
     */
    void updateUserPoints(Long userId, int points);
    /**
     * 更新用户会员等级
     * @param userId 用户ID
     * @param level 新的会员等级
     */
    void updateMemberLevel(Long userId, int level);
} 