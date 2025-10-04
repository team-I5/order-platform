package com.spartaclub.orderplatform.user.presentation.controller;

import com.spartaclub.orderplatform.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.user.application.service.UserService;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User 컨트롤러 클래스
 * 사용자 관련 API 엔드포인트 제공
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 API
     * POST /v1/users/signup
     * 신규 사용자 계정 생성 및 유효성 검증
     * 
     * @param requestDto 회원가입 요청 데이터 (사용자명, 이메일, 비밀번호, 닉네임, 연락처, 권한 등)
     * @return 회원가입 성공 시 생성된 사용자 ID와 성공 메시지 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = userService.signup(requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    /**
     * 로그인 API
     * POST /v1/users/login
     * 사용자 인증 후 JWT 토큰 발급
     * 
     * @param requestDto 로그인 요청 데이터 (이메일, 비밀번호)
     * @param response HTTP 응답 객체 (Authorization 헤더 설정용)
     * @return 로그인 성공 시 JWT 토큰과 사용자 정보 반환
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response) {
        
        UserLoginResponseDto responseDto = userService.login(requestDto);
        
        // Authorization 헤더에 Bearer 토큰 추가
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, 
                          JwtUtil.BEARER_PREFIX + responseDto.getAccessToken());
        
        return ResponseEntity.ok(responseDto);
    }
}