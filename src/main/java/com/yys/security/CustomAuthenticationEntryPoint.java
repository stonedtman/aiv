package com.yys.security;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 创建自定义的错误响应对象
        Result result = Result.success(HttpServletResponse.SC_UNAUTHORIZED,
                "还未登录，请先登录",
                0,
                null);

        // 使用Fastjson将对象转换为JSON字符串
        String json = JSON.toJSONString(result);

        // 输出JSON字符串
        response.getWriter().write(json);
    }
}