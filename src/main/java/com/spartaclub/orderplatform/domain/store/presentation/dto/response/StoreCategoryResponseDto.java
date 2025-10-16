package com.spartaclub.orderplatform.domain.store.presentation.dto.response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreCategoryResponseDto {

    private String storeName;
    private List<String> categories;
}
