package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주소 목록 응답 DTO 주소 목록 조회 시 개별 주소 정보를 담는 클래스
 *
 * @author 전우선
 * @date 2025-10-10(금)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressListResponseDto {

    // 주소 고유 ID (UUID)
    private UUID addressId;

    // 주소명 (예: 집, 회사, 본점)
    private String addressName;

    // 수령인 이름
    private String name;

    // 연락처
    private String phoneNumber;

    // 우편번호
    private String postCode;

    // 도로명 주소
    private String roadNameAddress;

    // 상세 주소
    private String detailedAddress;

    // 전체 주소 (도로명 주소 + 상세 주소)
    private String fullAddress;

    // 기본 주소 여부
    private Boolean defaultAddress;

    // 생성일시
    private LocalDateTime createdAt;

    // 수정일시
    private LocalDateTime modifiedAt;
}