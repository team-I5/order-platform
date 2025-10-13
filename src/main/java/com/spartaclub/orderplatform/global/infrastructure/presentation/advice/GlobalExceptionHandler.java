package com.spartaclub.orderplatform.global.infrastructure.presentation.advice; // 패키지 선언 - global.exception 패키지에 위치

import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 간단한 전역 예외 처리 핸들러 포스트맨 테스트용으로 기본적인 예외만 처리
 *
 * @author 전우선
 * @date 2025-10-05(일)
 */
@RestControllerAdvice // 모든 Controller에서 발생하는 예외를 전역적으로 처리하는 어노테이션
public class GlobalExceptionHandler { // 전역 예외 처리를 담당하는 클래스

    /**
     * 유효성 검증 실패 예외 처리 메서드
     *
     * @Valid 어노테이션으로 인한 검증 실패 시 호출 클라이언트 친화적인 에러 메시지 형태로 변환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // @Valid 검증 실패 예외 처리
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex) {

        // 에러 필드별로 메시지 추출하여 문자열로 결합
        StringBuilder errorMessage = new StringBuilder("유효성 검사 실패: ");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append(", ");
        }

        // 마지막 쉼표 제거
        String finalMessage = errorMessage.toString().replaceAll(", $", "");

        // 400 Bad Request로 응답 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // HTTP 400 상태코드 설정
            .body(ApiResponse.error(finalMessage)); // 상세 에러 메시지와 함께 에러 응답
    }

    /**
     * 일반 예외 처리 메서드 모든 예외를 포괄적으로 처리 (최종 안전망 역할)
     */
    @ExceptionHandler(Exception.class) // Exception 타입의 예외가 발생했을 때 이 메서드가 실행되도록 설정
    public ResponseEntity<ApiResponse<Void>> handleException(
        Exception e) { // Exception을 처리하는 핸들러 메서드
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 상태코드로 응답 설정
            .body(ApiResponse.error("에러 발생: " + e.getMessage())); // 에러 메시지와 함께 실패 응답 반환
    }

    /**
     * RuntimeException 처리 메서드 비즈니스 로직에서 발생하는 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class) // RuntimeException 타입의 예외가 발생했을 때 이 메서드가 실행되도록 설정
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
        RuntimeException e) { // RuntimeException을 처리하는 핸들러 메서드
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // HTTP 400 상태코드로 응답 설정 (클라이언트 요청 오류)
            .body(ApiResponse.error("요청 오류: " + e.getMessage())); // 에러 메시지와 함께 실패 응답 반환
    }
}