package com.spartaclub.orderplatform.domain.review.presentation.dto.request;


import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class ReviewSearchRequestDto {

    // foreign key
    private Long userId;
    private UUID storeId;
    private UUID orderId;
    private UUID productId;
    // option value
    private Integer rating;
    private String contents;
    // page set
    private int page = 0;
    private int size = 10;
    private Sort.Direction direction = Sort.Direction.DESC;

}
