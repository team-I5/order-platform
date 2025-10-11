package com.spartaclub.orderplatform.domain.store.application.mapper;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchByCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.domain.entity.UserRole;
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
    StoreSearchResponseDto toStoreSearchResponseDto(Store store);

    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "storeCategories", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "status", expression = "java(StoreStatus.PENDING)")
    @Mapping(target = "averageRating", expression = "java(0.0)")
    @Mapping(target = "reviewCount", expression = "java(0)")
    @Mapping(target = "createdId", expression = "java(user.getUserId())")
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    @Mapping(target = "rejectReason", ignore = true)
    Store toCreateStoreEntity(User user, StoreRequestDto dto);

    @Mapping(source = "storeName", target = "storeName")
    @Mapping(source = "storeAddress", target = "storeAddress")
    @Mapping(source = "storeNumber", target = "storeNumber")
    @Mapping(source = "storeDescription", target = "storeDescription")
    @Mapping(target = "status", expression = "java(role == UserRole.CUSTOMER ? null : store.getStatus())")
    @Mapping(target = "rejectReason", expression = "java(role == UserRole.CUSTOMER ? null : store.getRejectReason())")
    @Mapping(source = "averageRating", target = "averageRating")
    @Mapping(source = "reviewCount", target = "reviewCount")
    StoreDetailResponseDto toStoreDetailResponseDto(Store store, @Context UserRole role);

    @Mapping(target = "categories", expression = "java(mapCategories(store))")
    StoreCategoryResponseDto toStoreCategoryResponseDto(Store store);

    default List<String> mapCategories(Store store) {
        return store.getStoreCategories().stream()
            .map(storeCategory -> storeCategory.getCategory().getType().name()).toList();
    }

    @Mapping(target = "categories", ignore = true)
    StoreSearchByCategoryResponseDto toStoreSearchByCategoryResponseDto(Store store);
}
