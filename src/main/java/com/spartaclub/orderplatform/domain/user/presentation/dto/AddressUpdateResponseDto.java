package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주소 수정 응답 DTO
 *
 * @author 전우선
 * @date 2025-10-11(토)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateResponseDto {

    private String message;
    private UUID addressId;
    private String addressName;
    private String fullAddress;
    private Boolean defaultAddress;
    private LocalDateTime modifiedAt;
}