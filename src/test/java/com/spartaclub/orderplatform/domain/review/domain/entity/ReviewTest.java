package com.spartaclub.orderplatform.domain.review.domain.entity;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReviewTest {
    
    @Test
    @DisplayName("리뷰 객체 생성 with 정적 팩터리 메서드")
    void createReview_success() {
        // given
        Integer rating = 4;
        String contents = "맛이 좋습니다.";
        // when
        Review review = Review.create_test(rating, contents);
        //then
        Assertions.assertEquals(rating, review.getRating());
        Assertions.assertEquals(contents, review.getContents());
    }
}
