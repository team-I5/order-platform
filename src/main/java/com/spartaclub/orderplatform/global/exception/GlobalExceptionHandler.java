package com.spartaclub.orderplatform.global.exception; // 패키지 선언 - global.exception 패키지에 위치

import com.spartaclub.orderplatform.global.dto.ApiResponse; // 공통 응답 DTO 클래스 임포트
import org.springframework.http.HttpStatus; // HTTP 상태 코드 클래스 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답 엔티티 클래스 임포트
import org.springframework.web.bind.annotation.ExceptionHandler; // 예외 처리 어노테이션 임포트
import org.springframework.web.bind.annotation.RestControllerAdvice; // 전역 예외 처리 어노테이션 임포트

/**
 * 간단한 전역 예외 처리 핸들러
 * 포스트맨 테스트용으로 기본적인 예외만 처리
 * 
 * @author 전우선
 * @date 2025-09-30(화)
 */
@RestControllerAdvice // 모든 Controller에서 발생하는 예외를 전역적으로 처리하는 어노테이션
public class GlobalExceptionHandler { // 전역 예외 처리를 담당하는 클래스

    /**
     * 일반 예외 처리 메서드
     * 모든 예외를 포괄적으로 처리 (최종 안전망 역할)
     */
    @ExceptionHandler(Exception.class) // Exception 타입의 예외가 발생했을 때 이 메서드가 실행되도록 설정
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) { // Exception을 처리하는 핸들러 메서드
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 상태코드로 응답 설정
                .body(ApiResponse.error("에러 발생: " + e.getMessage())); // 에러 메시지와 함께 실패 응답 반환
    }

    /**
     * RuntimeException 처리 메서드
     * 비즈니스 로직에서 발생하는 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class) // RuntimeException 타입의 예외가 발생했을 때 이 메서드가 실행되도록 설정
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) { // RuntimeException을 처리하는 핸들러 메서드
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // HTTP 400 상태코드로 응답 설정 (클라이언트 요청 오류)
                .body(ApiResponse.error("요청 오류: " + e.getMessage())); // 에러 메시지와 함께 실패 응답 반환
    }
}