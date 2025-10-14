package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@NoArgsConstructor
public class CategorySearchRequestDto {

    // foreign key
    private UUID storeId;
    // option value
    private String categoryType;
    // page set
    private int page = 0;
    private int size = 10;
    private Sort.Direction direction = Direction.DESC;
}
