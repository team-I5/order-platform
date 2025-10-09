package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponseDto {

    private String storeName;
    private StoreStatus status;
}
