package com.scenic.mapper;

import com.scenic.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    Admin selectByUsername(String username);
    Admin selectById(Long id);
} 