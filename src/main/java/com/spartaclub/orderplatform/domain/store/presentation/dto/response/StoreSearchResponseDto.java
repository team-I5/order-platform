package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StoreSearchResponseDto {

    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
    private List<String> categories;
}
