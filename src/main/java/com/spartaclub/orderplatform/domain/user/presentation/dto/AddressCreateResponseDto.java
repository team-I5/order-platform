package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.util.UUID;

/**
 * 주소 등록 응답 DTO
 * 주소 등록 완료 시 반환되는 데이터
 *
 * @author 전우선
 * @date 2025-10-09(목)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateResponseDto {

    // 성공 메시지
    private String message;

    // 생성된 주소 ID (UUID)
    private UUID addressId;

    // 주소명
    private String addressName;

    // 전체 주소 (도로명 주소 + 상세 주소)
    private String fullAddress;
}