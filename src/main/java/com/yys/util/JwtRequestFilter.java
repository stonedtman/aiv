package com.yys.util;

import com.yys.entity.AiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {


        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        AiUser aiUser = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            aiUser = JwtUtil.extractUserDetails(jwt);
            logger.info("Extracted JWT: {}", jwt);
            logger.info("Extracted User: {}", aiUser);
        }

        if (aiUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String storedToken = redisTemplate.opsForValue().get(jwt);
            logger.info("Stored Token: {}", storedToken);

            if (storedToken != null && JwtUtil.validateToken(jwt, aiUser)) {
                UserDetails userDetails = new User(aiUser.getUserName(), "", JwtUtil.extractRoles(jwt));
                // 创建Authentication对象
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 设置Spring Security的上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set for user: {}", userDetails.getUsername());
            } else {
                logger.warn("Token validation failed or token not found.");
            }
        }

        chain.doFilter(request, response);
    }
}

