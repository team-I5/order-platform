package com.spartaclub.orderplatform.domain.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 주소 등록 요청 DTO 새로운 주소 등록 시 사용되는 요청 데이터
 *
 * @author 전우선
 * @date 2025-10-09(목)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateRequestDto {

    // 주소명 (예: 집, 회사, 본점)
    @NotBlank(message = "주소명은 필수입니다.")
    @Size(min = 1, max = 50, message = "주소명은 1-50자 이내여야 합니다.")
    private String addressName;

    // 수령인 이름
    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Size(min = 1, max = 50, message = "수령인 이름은 1-50자 이내여야 합니다.")
    private String name;

    // 연락처
    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "연락처는 10-13자리 숫자만 입력 가능합니다.")
    private String phoneNumber;

    // 우편번호
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(min = 1, max = 10, message = "우편번호는 1-10자 이내여야 합니다.")
    private String postCode;

    // 도로명 주소
    @NotBlank(message = "도로명 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "도로명 주소는 1-255자 이내여야 합니다.")
    private String roadNameAddress;

    // 상세 주소
    @NotBlank(message = "상세 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "상세 주소는 1-255자 이내여야 합니다.")
    private String detailedAddress;

    // 기본 주소 여부
    @Builder.Default
    private Boolean defaultAddress = false;
}