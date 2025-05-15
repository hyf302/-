package com.scenic.mapper;

import com.scenic.entity.MemberLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberLevelMapper {
    @Select("SELECT * FROM member_level WHERE min_points <= #{points} AND max_points >= #{points} AND status = 1 ORDER BY min_points DESC LIMIT 1")
    MemberLevel getLevelByPoints(@Param("points") Integer points);

    @Select("SELECT * FROM member_level WHERE min_points > #{points} AND status = 1 ORDER BY min_points ASC LIMIT 1")
    MemberLevel getNextLevel(@Param("points") Integer points);

    @Select("SELECT * FROM member_level WHERE status = 1 ORDER BY min_points ASC")
    List<MemberLevel> selectAll();

    void insertPointsLog(
        @Param("userId") Long userId, 
        @Param("pointsChange") Integer pointsChange, 
        @Param("reason") String reason
    );
} 
 