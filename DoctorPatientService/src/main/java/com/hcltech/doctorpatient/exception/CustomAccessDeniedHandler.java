package com.hcltech.doctorpatient.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{
    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        logger.error("Access denied: {}", accessDeniedException.getMessage(), accessDeniedException);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Access is denied\"}");
    }
}
