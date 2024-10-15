package com.yys.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // 创建自定义的错误响应对象
        Result result = Result.success(HttpServletResponse.SC_FORBIDDEN,
                "用户没有权限",
                0,
                null);

        // 使用Fastjson将对象转换为JSON字符串
        String json = JSON.toJSONString(result);

        // 输出JSON字符串
        response.getWriter().write(json);
    }
}
