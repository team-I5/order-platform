package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

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

    @NotBlank(message = "음식점명은 필수 입력 사항입니다.")
    @Size(max = 12, message = "음식점 명은 12자 이내로 설정해야 합니다.")
    private String storeName;

    @NotBlank(message = "음식점 주소는 필수 입력 사항입니다.")
    @Size(max = 80, message = "주소는 80자 이내로 입력해주세요.")
    private String storeAddress;

    @NotBlank(message = "음식점 전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "음식점 전화번호는 숫자 10-11자리여야 합니다.")
    private String storeNumber;

    @Size(max = 250, message = "음식점 소개글은 250자 이내로 작성해주세요.")
    private String storeDescription;
}
