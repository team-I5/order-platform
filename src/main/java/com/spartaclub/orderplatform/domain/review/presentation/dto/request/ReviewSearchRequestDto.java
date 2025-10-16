package com.spartaclub.orderplatform.domain.review.presentation.dto.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@RequiredArgsConstructor
public class ReviewSearchRequestDto extends ReviewCreateRequestDto {

    // page set
    private final int page;
    private final int size;
    private final Sort.Direction direction;

//    public void isRightPageSize() {
//        if (size != 10 || size !=30 || size != 50) {
//            this.size = 10;
//        }
//    }
}
