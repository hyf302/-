package com.scenic.mapper;

import com.scenic.entity.ScenicSpot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Options;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScenicSpotMapper extends BaseMapper<ScenicSpot> {
    @Select("SELECT * FROM scenic_spot WHERE " +
            "name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "description LIKE CONCAT('%', #{keyword}, '%') OR " +
            "address LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY create_time DESC")
    List<ScenicSpot> search(@Param("keyword") String keyword);
    
    @Select("SELECT id, name, description, address, " +
            "TIME_FORMAT(open_time, '%H:%i') as openTime, " +
            "TIME_FORMAT(close_time, '%H:%i') as closeTime, " +
            "max_capacity, price, image_urls, status, create_time, update_time " +
            "FROM scenic_spot ORDER BY create_time DESC")
    List<ScenicSpot> selectList();
    
    @Select("SELECT * FROM scenic_spot WHERE id = #{id}")
    ScenicSpot selectById(Long id);
    
    @Insert("INSERT INTO scenic_spot (name, description, address, open_time, close_time, " +
            "max_capacity, price, image_urls, status, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{address}, #{openTime}, #{closeTime}, " +
            "#{maxCapacity}, #{price}, #{imageUrls}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScenicSpot scenicSpot);
    
    @Update("UPDATE scenic_spot SET name = #{name}, description = #{description}, " +
            "address = #{address}, open_time = #{openTime}, close_time = #{closeTime}, " +
            "max_capacity = #{maxCapacity}, price = #{price}, image_urls = #{imageUrls}, " +
            "status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int update(ScenicSpot scenicSpot);
    
    @Delete("DELETE FROM scenic_spot WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM scenic_spot")
    long countTotal();
} 