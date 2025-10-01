package com.spartaclub.orderplatform.domain.user.dto; // User DTO 패키지 선언

import lombok.AllArgsConstructor; // Lombok AllArgsConstructor 어노테이션
import lombok.Getter; // Lombok Getter 어노테이션

/**
 * 회원가입 응답 DTO 클래스
 * 회원가입 성공 시 클라이언트에게 반환할 데이터
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성
@AllArgsConstructor // Lombok - 모든 필드를 매개변수로 받는 생성자 자동 생성
public class UserSignupResponseDto {

    private String message; // 성공 메시지
    private Long userId; // 생성된 사용자 ID

    /**
     * 회원가입 성공 응답 생성 정적 메서드
     * 
     * @param userId 생성된 사용자 ID
     * @return 회원가입 성공 응답 DTO
     */
    public static UserSignupResponseDto success(Long userId) {
        return new UserSignupResponseDto("회원가입이 완료되었습니다.", userId); // 성공 메시지와 사용자 ID 반환
    }
}