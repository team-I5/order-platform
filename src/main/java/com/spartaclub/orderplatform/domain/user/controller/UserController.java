package com.spartaclub.orderplatform.domain.user.controller; // User 컨트롤러 패키지 선언

import com.spartaclub.orderplatform.domain.user.dto.UserSignupRequestDto; // 회원가입 요청 DTO 임포트
import com.spartaclub.orderplatform.domain.user.dto.UserSignupResponseDto; // 회원가입 응답 DTO 임포트
import com.spartaclub.orderplatform.domain.user.service.UserService; // User 서비스 임포트
import com.spartaclub.orderplatform.global.dto.ApiResponse; // 전역 API 응답 DTO 임포트
import jakarta.validation.Valid; // 유효성 검증 어노테이션 임포트
import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드 임포트
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트
import org.springframework.web.bind.annotation.*; // Spring Web 어노테이션 임포트

/**
 * User 컨트롤러 클래스
 * 사용자 관련 API 엔드포인트 제공
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@RestController // REST API 컨트롤러로 등록
@RequestMapping("/v1/users") // API 버전 및 기본 경로 설정
@RequiredArgsConstructor // Lombok - final 필드에 대한 생성자 자동 생성
public class UserController {

    private final UserService userService; // User 서비스 의존성 주입

    /**
     * 회원가입 API
     * POST /v1/users/signup
     * 
     * @param requestDto 회원가입 요청 데이터 (유효성 검증 포함)
     * @return 회원가입 응답 데이터
     */
    @PostMapping("/signup") // POST 요청 매핑
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        
        // 회원가입 처리
        UserSignupResponseDto responseDto = userService.signup(requestDto); // 서비스 레이어 호출
        
        // 성공 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created 상태 코드
                .body(ApiResponse.success(responseDto)); // ApiResponse로 감싸서 반환
    }
}