package com.lyc.learn.AuthService.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKeyForJwtSigningAndVerification2026}")
    private String secret;

    @Value("${jwt.expire:1800000}")  // 默认30分钟
    private Long expire;
    
    // token过期阈值，临近这个时间的token，会重新生成新的token，默认5分钟
    @Value("${jwt.refreshThreshold:300000}")
    private Long refreshThreshold;
    
    public Long getExpire() {
        return expire;
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT
     * @param claims 自定义载荷（如 userId, username, roleCodes 等）
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.get("userId").toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT 获取 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean tokenIsApproachExpire(String token) {
        Date expiration = parseToken(token).getExpiration();
        return System.currentTimeMillis() + refreshThreshold >= expiration.getTime();
    }

    /**
     * 验证 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
