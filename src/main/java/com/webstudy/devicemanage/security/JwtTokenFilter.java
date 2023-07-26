package com.webstudy.devicemanage.security;

import com.webstudy.devicemanage.exception.CustomException;
import com.webstudy.devicemanage.model.PasswordRecord;
//import com.webstudy.devicemanage.repository.PasswordRepository;
import com.webstudy.devicemanage.service.UserService;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, Object> redisTemplate;

//    private  PasswordRepository passwordRepository;



    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

//    @Value("${security.jwt.token-renew-length}")
    //TODO:修改redis密钥有效期
    private long validityInMilliseconds = 1800000;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 获取用户名，查询redis验证token有效性
                String userName = jwtTokenProvider.getUsername(token);
                isJwtValid(userName, token);
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException exception) {
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(exception.getHttpStatus().value(), exception.getMessage());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean isJwtValid(String userName, String token) {
        // 验证JWT是否有效
        String redisKey = "jwt:"+userName;
        try {
            // 从Redis中获取对应用户的Token
            String redisValue = redisTemplate.opsForValue().get(redisKey).toString();
            // 由于Redis设置了TTL，所以如果过期会自动销毁
            if(! redisValue.equals(token)){
                throw new CustomException("登录身份过期，请重新登陆！", HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception err){
            throw new CustomException("登录身份过期，请重新登陆！", HttpStatus.UNAUTHORIZED);
        }

        // 若有效，则对Redis Key进行续期
        redisTemplate.opsForValue().set(redisKey, token, validityInMilliseconds, TimeUnit.MILLISECONDS);
        return true;
    }

//    private boolean isDateValid(String userName){
//        // 检查密码是否过期
//        try {
//            List<PasswordRecord> passwordRecords = passwordRepository.getPasswordRecordsByUserName(userName);
//        }catch (Exception exception){
//            throw new CustomException("检查密码历史失败", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return true;
//    }
}
