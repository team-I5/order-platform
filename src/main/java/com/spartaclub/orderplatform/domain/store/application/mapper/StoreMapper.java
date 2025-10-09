package com.spartaclub.orderplatform.domain.store.application.mapper;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(source = "storeName", target = "storeName")
    @Mapping(source = "status", target = "status")
    StoreResponseDto toStoreResponseDto(Store store);

    @Mapping(source = "storeName", target = "storeName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "rejectReason", target = "rejectReason")
    RejectStoreResponseDto toRejectStoreResponseDto(Store store);

    @Mapping(source = "storeName", target = "storeName")
    @Mapping(source = "averageRating", target = "averageRating")
    @Mapping(source = "reviewCount", target = "reviewCount")
    StoreSearchResponseDto toStoreSearchResponseDto(Store store);
}
