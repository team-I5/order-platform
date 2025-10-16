package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/*
 * 카테고리 검색 요청 dto
 *
 * @author 이준성
 * @date 2025-10-14
 */
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
