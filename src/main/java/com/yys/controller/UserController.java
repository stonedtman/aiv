package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.AiUser;
import com.yys.entity.Result;
import com.yys.service.UserService;
import com.yys.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/login")
    public String login(@RequestBody AiUser aiUser) {

        AiUser user = userService.login(aiUser);
        if (user == null){
            return JSON.toJSONString(Result.success(500,"用户名或密码错误",0,"用户名或密码错误"));
        }
        String token = JwtUtil.generateToken(user);
        user.setToken(token);
        redisTemplate.opsForValue().set(token, user.getUserName(), 10, TimeUnit.HOURS);
        return JSON.toJSONString(Result.success("登陆成功",1,user));
    }

    @RequestMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return JSON.toJSONString(Result.error("未提供Token"));
        }
        String jwt = token.substring(7);
        // 检查Token是否存在于Redis中
        Boolean hasToken = redisTemplate.hasKey(jwt);
        if (Boolean.TRUE.equals(hasToken)) {
            // 删除Token
            redisTemplate.delete(jwt);
            return JSON.toJSONString(Result.success("登出成功"));
        } else {
            return JSON.toJSONString(Result.error("无效的Token"));
        }
    }


    @GetMapping("/changePassword")
    public String changePassword(@RequestHeader("Authorization") String token,
                                 @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                 @RequestParam(value = "newPassword", required = false) String newPassword) {

        if (token == null || token.isEmpty()) {
            return JSON.toJSONString(Result.error("Token不能为空"));
        }

        String jwt = token.substring(7);
        try {
            // 解析 token

            String userName = jwtUtil.extractUsername(jwt);
            String tokenOldPassword = jwtUtil.extractuserPwd(jwt);
            String userId = jwtUtil.extractUserId(jwt);

            // 验证旧密码
            if (!tokenOldPassword.equals(oldPassword)) {
                return JSON.toJSONString(Result.success(500,"旧密码不正确",0,"旧密码不正确"));
            }

            // 从数据库中获取用户信息
            AiUser user = new AiUser();
            user.setId(userId);
            user.setUserName(userName);
            user.setUserPwd(newPassword);
            // 更新数据库中的密码
            int i=userService.updateUserPassword(user);
            if (i==0){
                return JSON.toJSONString(Result.success(500,"修改密码失败",0,"修改密码失败"));
            }
            // 删除 Redis 中的 token
            redisTemplate.delete(jwt);
            return JSON.toJSONString(Result.success("密码修改成功，请重新登录"));
        } catch (ExpiredJwtException e) {
            return JSON.toJSONString(Result.success(500,"Token已过期，请重新登录",0,"Token已过期，请重新登录"));
        } catch (Exception e) {
            return JSON.toJSONString(Result.success(500,"修改密码失败：" + e.getMessage(),0,"修改密码失败：" + e.getMessage()));
        }
    }
}
