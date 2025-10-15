package com.spartaclub.orderplatform.domain.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주소 수정 요청 DTO
 *
 * @author 전우선
 * @date 2025-10-11(토)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequestDto {

    @NotBlank(message = "주소명은 필수입니다.")
    @Size(min = 1, max = 50, message = "주소명은 1-50자 이내로 입력해주세요.")
    private String addressName;

    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Size(min = 1, max = 50, message = "수령인 이름은 1-50자 이내로 입력해주세요.")
    private String name;

    @NotBlank(message = "연락처는 필수입니다.")
    @Size(min = 10, max = 13, message = "연락처는 10-13자리로 입력해주세요.")
    private String phoneNumber;

    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(min = 1, max = 10, message = "우편번호는 1-10자 이내로 입력해주세요.")
    private String postCode;

    @NotBlank(message = "도로명 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "도로명 주소는 1-255자 이내로 입력해주세요.")
    private String roadNameAddress;

    @NotBlank(message = "상세 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "상세 주소는 1-255자 이내로 입력해주세요.")
    private String detailedAddress;

    @NotNull(message = "기본 주소 설정은 필수입니다.")
    private Boolean defaultAddress;
}