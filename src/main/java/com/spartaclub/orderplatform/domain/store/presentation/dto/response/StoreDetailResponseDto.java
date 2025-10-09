package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreDetailResponseDto {

    private String storeName;
    private String storeAddress;
    private String storeNumber;
    private String storeDescription;
    private StoreStatus status;
    private String rejectReason;
    private Double averageRating;
    private Integer reviewCount;
}
