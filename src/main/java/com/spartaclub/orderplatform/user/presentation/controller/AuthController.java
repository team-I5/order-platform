package com.spartaclub.orderplatform.user.presentation.controller;

import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import com.spartaclub.orderplatform.user.application.service.UserService;
import com.spartaclub.orderplatform.user.presentation.dto.TokenRefreshRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.TokenRefreshResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 컨트롤러 클래스 JWT 토큰 관련 API 엔드포인트 제공
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 토큰 갱신 API POST /v1/auth/refresh RTR(Refresh Token Rotation) 패턴으로 새로운 액세스/리프레시 토큰 발급
     *
     * @param requestDto 토큰 갱신 요청 데이터 (리프레시 토큰)
     * @param response   HTTP 응답 객체 (Authorization 헤더 설정용)
     * @return 토큰 갱신 성공 시 새로운 JWT 토큰과 사용자 정보 반환
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
        @Valid @RequestBody TokenRefreshRequestDto requestDto,
        HttpServletResponse response) {

        TokenRefreshResponseDto responseDto = userService.refreshToken(requestDto);

        // Authorization 헤더에 새로운 Bearer 토큰 추가
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER,
            JwtUtil.BEARER_PREFIX + responseDto.getAccessToken());

        return ResponseEntity.ok(responseDto);
    }
}