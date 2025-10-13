package com.spartaclub.orderplatform.user.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주소 삭제 응답 DTO
 *
 * @author 전우선
 * @date 2025-10-12(일)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDeleteResponseDto {

    private String message;
    private UUID deletedAddressId;
    private String deletedAddressName;
    private LocalDateTime deletedAt;
}