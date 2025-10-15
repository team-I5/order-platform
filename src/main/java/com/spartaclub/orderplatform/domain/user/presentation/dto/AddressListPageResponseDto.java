package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * 주소 목록 페이지 응답 DTO
 * 주소 목록과 통계 정보를 포함한 완전한 응답 클래스
 *
 * @author 전우선
 * @date 2025-10-10(금)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressListPageResponseDto {

    // 주소 목록
    private List<AddressListResponseDto> addresses;

    // 총 주소 개수
    private long totalCount;

    // 기본 주소 정보
    private DefaultAddressInfo defaultAddress;

    /**
     * 기본 주소 정보 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DefaultAddressInfo {
        private UUID addressId;
        private String addressName;
    }
}