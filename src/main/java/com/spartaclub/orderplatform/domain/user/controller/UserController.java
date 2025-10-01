package com.spartaclub.orderplatform.domain.user.controller;

import com.spartaclub.orderplatform.domain.user.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.domain.user.service.UserService;
import com.spartaclub.orderplatform.global.dto.ApiResponse;
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

    /**
     * 회원가입 API
     * POST /v1/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = userService.signup(requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }
}