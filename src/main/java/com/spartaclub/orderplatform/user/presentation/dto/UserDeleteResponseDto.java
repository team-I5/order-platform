package com.spartaclub.orderplatform.user.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원 탈퇴 응답 DTO
 * 회원 탈퇴 완료 시 클라이언트에게 반환되는 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-04(토)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDeleteResponseDto {

    // 탈퇴 완료 메시지
    private String message;

    // 탈퇴 처리 일시
    private LocalDateTime deletedAt;

    // 탈퇴한 사용자 ID
    private Long userId;

    /**
     * 회원 탈퇴 완료 응답 DTO 생성자
     *
     * @param userId    탈퇴한 사용자 ID
     * @param deletedAt 탈퇴 처리 일시
     */
    public UserDeleteResponseDto(Long userId, LocalDateTime deletedAt) {
        this.message = "회원 탈퇴가 완료되었습니다.";
        this.userId = userId;
        this.deletedAt = deletedAt;
    }

    /**
     * 회원 탈퇴 완료 응답 생성 정적 메서드
     *
     * @param userId    탈퇴한 사용자 ID
     * @param deletedAt 탈퇴 처리 일시
     * @return UserDeleteResponseDto 인스턴스
     */
    public static UserDeleteResponseDto success(Long userId, LocalDateTime deletedAt) {
        return new UserDeleteResponseDto(userId, deletedAt);
    }
}