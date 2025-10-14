package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRequestDto {

    @Column(nullable = false)
    private String name;
//    @Column(nullable = false, length = 20)
//    private String storeName;
}
