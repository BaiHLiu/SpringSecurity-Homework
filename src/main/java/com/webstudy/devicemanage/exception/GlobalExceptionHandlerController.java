package com.webstudy.devicemanage.exception;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

                return errorAttributes;
            }
        };
    }



    @ExceptionHandler(CustomException.class)
    public void handleCustomException(HttpServletResponse response, CustomException exception) throws IOException {
        response.sendError(exception.getHttpStatus().value(), exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "访问被拒绝");
    }

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "内部错误，请联系管理员");
    }
}