package com.yys.util;

import com.yys.entity.AiUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "swerrr7";

    public static String generateToken(AiUser aiUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", aiUser.getPermissions());
        claims.put("userName", aiUser.getUserName());
        claims.put("userPwd", aiUser.getUserPwd());
        claims.put("userId", aiUser.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(aiUser.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        return extractClaims(token).get("userName", String.class);
    }

    public static String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }

    public static String extractuserPwd(String token) {
        return extractClaims(token).get("userPwd", String.class);
    }
    public static List<GrantedAuthority> extractRoles(String token) {
        String rolesString = extractClaims(token).get("permissions", String.class);
        return Arrays.stream(rolesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public static AiUser extractUserDetails(String token) {
        Claims claims = extractClaims(token);
        String userId = claims.get("userId", String.class);
        String userName = claims.getSubject();
        List<GrantedAuthority> permissions = extractRoles(token);

        AiUser aiUser = new AiUser();
        aiUser.setId(userId);
        aiUser.setUserName(userName);
        aiUser.setPermissions(claims.get("permissions", String.class)); // Keep as string

        return aiUser;
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean validateToken(String token, AiUser aiUser) {
        final String username = extractUsername(token);
        return (username.equals(aiUser.getUserName()) && !isTokenExpired(token));
    }
}


