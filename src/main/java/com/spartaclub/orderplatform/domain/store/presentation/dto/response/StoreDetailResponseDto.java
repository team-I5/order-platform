package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StoreDetailResponseDto {

    private String storeName;
    private String storeAddress;
    private String storeNumber;
    private String storeDescription;
    private List<String> categories;
    private StoreStatus status;
    private String rejectReason;
    private Double averageRating;
    private Integer reviewCount;
}
