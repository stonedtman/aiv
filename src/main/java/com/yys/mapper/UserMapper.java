package com.yys.mapper;

import com.yys.entity.AiUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    int insert(AiUser aiUser);

    AiUser login(AiUser aiUser);

    @Select("SELECT * FROM ai_user WHERE user_name = #{userName}")
    AiUser getUserByUserName(@Param("userName")String userName);

    @Update("UPDATE ai_user SET user_pwd = #{userPwd} WHERE id = #{id}")
    int updateUserPassword(AiUser aiUser);
}
