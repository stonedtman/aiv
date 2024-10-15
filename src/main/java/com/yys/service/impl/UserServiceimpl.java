package com.yys.service.impl;

import com.yys.entity.AiUser;
import com.yys.entity.Result;
import com.yys.mapper.UserMapper;
import com.yys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public AiUser login(AiUser aiUser) {
        AiUser user = userMapper.login(aiUser);
        return user;
    }

    @Override
    public AiUser getUserByUserName(String userName) {
        return userMapper.getUserByUserName(userName);
    }

    @Override
    public int updateUserPassword(AiUser aiUser) {
        return userMapper.updateUserPassword(aiUser);
    }

}
