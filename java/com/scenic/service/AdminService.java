package com.scenic.service;

import com.scenic.entity.Admin;

public interface AdminService {
    Admin login(String username, String password);
    Admin getAdminByUsername(String username);
} 