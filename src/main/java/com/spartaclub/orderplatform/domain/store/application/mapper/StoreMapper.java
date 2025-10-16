package com.spartaclub.orderplatform.domain.store.application.mapper;


import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import java.util.List;
import org.mapstruct.Context;
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
    @Mapping(target = "categories", expression = "java(mapCategories(store))")
    StoreSearchResponseDto toStoreSearchResponseDto(Store store);

    @Mapping(source = "storeName", target = "storeName")
    @Mapping(source = "storeAddress", target = "storeAddress")
    @Mapping(source = "storeNumber", target = "storeNumber")
    @Mapping(source = "storeDescription", target = "storeDescription")
    @Mapping(target = "categories", expression = "java(mapCategories(store))")
    @Mapping(target = "status", expression = "java(role == UserRole.CUSTOMER ? null : store.getStatus())")
    @Mapping(target = "rejectReason", expression = "java(role == UserRole.CUSTOMER ? null : store.getRejectReason())")
    @Mapping(source = "averageRating", target = "averageRating")
    @Mapping(source = "reviewCount", target = "reviewCount")
    StoreDetailResponseDto toStoreDetailResponseDto(Store store, @Context UserRole role);

    @Mapping(target = "categories", expression = "java(mapCategories(store))")
    StoreCategoryResponseDto toStoreCategoryResponseDto(Store store);

    default List<String> mapCategories(Store store) {
        return store.getStoreCategories().stream()
            .map(storeCategory -> storeCategory.getCategory().getType()).toList();
    }
}
