package com.scenic.mapper;

import com.scenic.entity.PointsRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PointsRecordMapper {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);
    
    @Insert("INSERT INTO points_record (user_id, points, type, description, create_time) " +
           "VALUES (#{record.userId}, #{record.points}, #{record.type}, #{record.description}, #{record.createTime})")
    int insertRecord(@Param("record") PointsRecord record);
    
    @Select("SELECT COALESCE(SUM(points), 0) FROM points_record WHERE user_id = #{userId}")
    int sumPointsByUserId(@Param("userId") Long userId);
    
    void insertPointsLog(
        @Param("userId") Long userId, 
        @Param("pointsChange") Integer pointsChange, 
        @Param("reason") String reason
    );
} 
 