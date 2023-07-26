package com.webstudy.devicemanage.security;

import com.webstudy.devicemanage.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secretKey;
    @Value("${security.jwt.token-expire-length}")
    private long validityInMilliseconds;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MyUserDetails myUserDetails;

    public JwtTokenProvider(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();

        // 存入redis缓存
        String redisKey = "jwt:" + username;
        try {
            redisTemplate.opsForValue().set(redisKey, jwtToken, validityInMilliseconds, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            throw new CustomException("Can not cache your token!", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return jwtToken;
    }

    public void deleteToken(String username) {
        String redisKey = "jwt:" + username;

        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                redisTemplate.delete(redisKey);
            } else {
                throw new CustomException("Token not found in Redis!", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new CustomException("Error deleting token from Redis!", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    public String getUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
