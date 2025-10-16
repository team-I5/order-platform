package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreRequestDto {

    @Schema(description = "음식점 이름", example = "김밥나라")
    @NotBlank(message = "음식점명은 필수 입력 사항입니다.")
    @Size(max = 12, message = "음식점 명은 12자 이내로 설정해야 합니다.")
    private String storeName;

    @Schema(description = "음식점 주소", example = "서울특별시 종로구 사직로 161")
    @NotBlank(message = "음식점 주소는 필수 입력 사항입니다.")
    @Size(max = 80, message = "주소는 80자 이내로 입력해주세요.")
    private String storeAddress;

    @Schema(description = "음식점 전화번호", example = "0212345678")
    @NotBlank(message = "음식점 전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "음식점 전화번호는 숫자 10-11자리여야 합니다.")
    private String storeNumber;

    @Schema(description = "음식점 소개글", example = "최고의 맛으로 승부합니다.")
    @Size(max = 250, message = "음식점 소개글은 250자 이내로 작성해주세요.")
    private String storeDescription;
}
