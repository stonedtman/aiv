package com.yys.service;

import com.yys.entity.AiUser;
import com.yys.entity.Result;
import org.apache.ibatis.annotations.Param;

public interface UserService {
    AiUser login(AiUser aiUser);

    AiUser getUserByUserName(String userName);

    int updateUserPassword(AiUser aiUser);
}
