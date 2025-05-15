package com.scenic.mapper;

import com.scenic.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    int insert(User user);
    User selectById(Long id);
    User selectByUsername(String username);
    User selectByEmail(String email);
    User selectByPhone(String phone);
    int update(User user);
    @Select("SELECT * FROM sys_user")
    List<User> selectList();
    
    /**
     * 更新用户积分
     */
    void updatePoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Select("SELECT COUNT(*) FROM sys_user")
    long countTotal();

    @Select("SELECT * FROM sys_user WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR phone LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%')")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Update("UPDATE sys_user SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE sys_user SET " +
            "phone = #{phone}, " +
            "email = #{email}, " +
            "member_level = #{memberLevel}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateUser(User user);
} 