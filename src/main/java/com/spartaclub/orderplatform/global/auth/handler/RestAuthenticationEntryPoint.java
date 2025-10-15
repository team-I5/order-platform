package com.spartaclub.orderplatform.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.global.auth.exception.AuthErrorCode;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

//미인증 or 인증 실패 핸들러
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
        AuthenticationException ex)
        throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json;charset=UTF-8");
        var body = ApiResponse.error(AuthErrorCode.UNAUTHENTICATED);
        om.writeValue(res.getWriter(), body);
    }
}
