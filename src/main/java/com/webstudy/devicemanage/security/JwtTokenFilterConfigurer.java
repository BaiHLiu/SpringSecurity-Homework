package com.webstudy.devicemanage.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;


    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenProvider,  redisTemplate);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
