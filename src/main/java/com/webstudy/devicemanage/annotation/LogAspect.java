package com.webstudy.devicemanage.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webstudy.devicemanage.model.LogEntity;
import com.webstudy.devicemanage.service.LogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private LogService logService;

    @Around("@annotation(com.webstudy.devicemanage.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long endTime = System.currentTimeMillis();

        LogEntity logEntity = new LogEntity();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String operation = getMethodDescription(point);
        String method = request.getMethod();
        String params = JSONObject.toJSONString(request.getParameterMap());
        String ip = getIpAddr(request);

        logEntity.setUsername(username);
        logEntity.setOperation(operation);
        logEntity.setMethod(method);
        logEntity.setParams(params);
        logEntity.setIp(ip);
        logEntity.setCreateTime(new Date());

        logService.saveLog(logEntity);

        return result;
    }

    private String getMethodDescription(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        return logAnnotation.value();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    try {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null) {
                if (ipAddress.contains(",")) {
                    return ipAddress.split(",")[0];
                } else {
                    return ipAddress;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}