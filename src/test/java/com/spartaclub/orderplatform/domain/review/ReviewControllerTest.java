package com.spartaclub.orderplatform.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.controller.ReviewController;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    private ReviewController reviewController;

    private User customer1;

    @BeforeEach
    void setUp() {
        reviewController = new ReviewController(reviewService);

        customer1 = User.createUser(
            "customer1",
            "customer1@test.com",
            "Hashpassword1",
            "고객1",
            "01012345678",
            UserRole.CUSTOMER
        );
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("리뷰 생성 성공")
    public void createReview_success() {

        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            4,
            "배달이 빨랐어요!"
        );

        ReviewResponseDto responseDto = new ReviewResponseDto(
            UUID.randomUUID(), 1L, requestDto.getStoreId(),
            requestDto.getOrderId(), requestDto.getProductId(),
            requestDto.getRating(), requestDto.getContents()
        );

        when(reviewService.createReview(any(User.class), any(ReviewCreateRequestDto.class)))
            .thenReturn(responseDto);
        ResponseEntity<ApiResponse<ReviewResponseDto>> rlt =
            reviewController.createReview(new UserDetailsImpl(customer1), requestDto);
        .and

    }

}