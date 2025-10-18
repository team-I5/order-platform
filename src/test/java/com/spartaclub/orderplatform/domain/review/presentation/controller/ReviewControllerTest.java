package com.spartaclub.orderplatform.domain.review.presentation.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

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
        UserDetailsImpl userDetails = new UserDetailsImpl(customer1);
        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            4,
            "배달이 빨랐어요!"
        );

        ReviewResponseDto responseDto = new ReviewResponseDto(
            UUID.randomUUID(), customer1.getUserId(), requestDto.getStoreId(),
            requestDto.getOrderId(), requestDto.getProductId(),
            requestDto.getRating(), requestDto.getContents()
        );

        when(reviewService.createReview(customer1, requestDto)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<ReviewResponseDto>> rlt =
            reviewController.createReview(userDetails, requestDto);
        Assertions.assertEquals(HttpStatus.CREATED, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("리뷰 수정 성공")
    public void updateReview_success() {
        UserDetailsImpl userDetails = new UserDetailsImpl(customer1);
        UUID reviewId = UUID.randomUUID();
        ReviewUpdateRequestDto requestDto = ReviewUpdateRequestDto.of(3, "음식이 식었어요!");

        ReviewResponseDto responseDto = new ReviewResponseDto(
            reviewId, customer1.getUserId(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(),
            requestDto.getRating(), requestDto.getContents()
        );
        when(reviewService.updateReview(customer1, reviewId, requestDto)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<ReviewResponseDto>> rlt =
            reviewController.updateReview(userDetails, reviewId, requestDto);
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("리뷰 삭제 성공")
    public void deleteReview_success() {
        UserDetailsImpl userDetails = new UserDetailsImpl(customer1);
        UUID reviewId = UUID.randomUUID();

        doNothing().when(reviewService).deleteReview(customer1, reviewId);
        ResponseEntity<ApiResponse<Void>> rlt =
            reviewController.deleteReview(userDetails, reviewId);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, rlt.getStatusCode());
        verify(reviewService).deleteReview(customer1, reviewId);
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    public void searchDetailsReview_success() {
        UUID reviewId = UUID.randomUUID();
        ReviewSearchResponseDto responseDto = new ReviewSearchResponseDto(
            3, "배송이 늦었어요!"
        );
        when(reviewService.searchDetailReview(reviewId)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<ReviewSearchResponseDto>> rlt =
            reviewController.searchDetailReview(reviewId);
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }
}