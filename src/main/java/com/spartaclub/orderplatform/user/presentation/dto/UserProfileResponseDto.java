package com.spartaclub.orderplatform.user.presentation.dto;

import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원정보 조회 응답 DTO
 * 사용자 프로필 정보를 클라이언트에게 반환하는 데이터 전송 객체
 * 민감정보(비밀번호, 삭제일)는 제외하고 필요한 정보만 포함
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponseDto {

    // 사용자 고유 ID
    private Long userId;

    // 사용자명
    private String username;

    // 이메일 주소
    private String email;

    // 닉네임
    private String nickname;

    // 연락처
    private String phoneNumber;

    // 사업자번호 (OWNER 권한만 값 존재, CUSTOMER는 null)
    private String businessNumber;

    // 사용자 권한
    private String role;

    // 계정 생성일
    private LocalDateTime createdAt;

    // 마지막 수정일
    private LocalDateTime modifiedAt;

    /**
     * User 엔티티로부터 UserProfileResponseDto 생성
     * 민감정보는 제외하고 필요한 정보만 매핑
     *
     * @param user User 엔티티 객체
     */
    public UserProfileResponseDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.businessNumber = user.getBusinessNumber(); // CUSTOMER의 경우 null
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
    }

    /**
     * User 엔티티로부터 응답 DTO 생성하는 정적 팩토리 메서드
     *
     * @param user User 엔티티 객체
     * @return UserProfileResponseDto 인스턴스
     */
    public static UserProfileResponseDto from(User user) {
        return new UserProfileResponseDto(user);
    }
}