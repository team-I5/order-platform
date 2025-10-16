package com.spartaclub.orderplatform.domain.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주소 수정 요청 정보")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequestDto {

    @Schema(description = "수정할 주소명", example = "새로운 집")
    @NotBlank(message = "주소명은 필수입니다.")
    @Size(min = 1, max = 50, message = "주소명은 1-50자 이내로 입력해주세요.")
    private String addressName;

    @Schema(description = "수정할 수령인 이름", example = "김철수")
    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Size(min = 1, max = 50, message = "수령인 이름은 1-50자 이내로 입력해주세요.")
    private String name;

    @Schema(description = "수정할 연락처", example = "01087654321")
    @NotBlank(message = "연락처는 필수입니다.")
    @Size(min = 10, max = 13, message = "연락처는 10-13자리로 입력해주세요.")
    private String phoneNumber;

    @Schema(description = "수정할 우편번호", example = "54321")
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(min = 1, max = 10, message = "우편번호는 1-10자 이내로 입력해주세요.")
    private String postCode;

    @Schema(description = "수정할 도로명 주소", example = "부산시 해운대구 센텀시티대로 99")
    @NotBlank(message = "도로명 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "도로명 주소는 1-255자 이내로 입력해주세요.")
    private String roadNameAddress;

    @Schema(description = "수정할 상세 주소", example = "202동 2002호")
    @NotBlank(message = "상세 주소는 필수입니다.")
    @Size(min = 1, max = 255, message = "상세 주소는 1-255자 이내로 입력해주세요.")
    private String detailedAddress;

    @Schema(description = "기본 주소 설정 여부", example = "true")
    @NotNull(message = "기본 주소 설정은 필수입니다.")
    private Boolean defaultAddress;
}