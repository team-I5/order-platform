package com.spartaclub.orderplatform.user.presentation.dto;

import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원정보 수정 응답 DTO
 * 회원정보 수정 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateResponseDto {

    // 수정 성공 메시지
    private String message;

    // 수정된 사용자 정보
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     * 수정된 사용자 정보를 담는 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String nickname;
        private String phoneNumber;
        private String businessNumber;
        private String role;
        private LocalDateTime modifiedAt;

        /**
         * User 엔티티로부터 UserInfo 객체 생성
         *
         * @param user User 엔티티 객체
         */
        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.phoneNumber = user.getPhoneNumber();
            this.businessNumber = user.getBusinessNumber();
            this.role = user.getRole().name();
            this.modifiedAt = user.getModifiedAt();
        }
    }

    /**
     * 회원정보 수정 성공 응답 DTO 생성자
     *
     * @param user 수정된 사용자 엔티티
     */
    public UserUpdateResponseDto(User user) {
        this.message = "회원정보가 수정되었습니다.";
        this.user = new UserInfo(user);
    }

    /**
     * 회원정보 수정 성공 응답 생성 정적 메서드
     *
     * @param user 수정된 사용자 엔티티
     * @return UserUpdateResponseDto 인스턴스
     */
    public static UserUpdateResponseDto success(User user) {
        return new UserUpdateResponseDto(user);
    }
}