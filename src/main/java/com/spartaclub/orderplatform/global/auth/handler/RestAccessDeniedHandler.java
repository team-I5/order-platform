package com.spartaclub.orderplatform.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.global.auth.exception.AuthErrorCode;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// 인가 실패 핸들러
@Component
@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
        throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (auth != null ? auth.getName() : "anonymous");
        String roles = (auth != null ? auth.getAuthorities().toString() : "none");

        log.warn("Access denied - path={}, method={}, principal={}, roles={}",
            req.getRequestURI(), req.getMethod(), principal, roles);

        // 표준 응답 바디
        res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        res.setContentType("application/json;charset=UTF-8");
        var body = ApiResponse.error(AuthErrorCode.FORBIDDEN);
        om.writeValue(res.getWriter(), body);
    }
}
