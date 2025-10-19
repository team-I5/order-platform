package com.spartaclub.orderplatform.domain.review.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private MockMvc mockMvc;
    @Mock
    private ReviewService reviewService;
    @Mock
    private ReviewMapper reviewMapper;
    @InjectMocks
    private ReviewController reviewController;

    private User customer1;
    private UserDetailsImpl userDetails;
    private UUID reviewId;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        customer1 = User.createUser(
            "customer1",
            "customer1@test.com",
            "Hashpassword1",
            "고객1",
            "01012345678",
            UserRole.CUSTOMER
        );
        userDetails = new UserDetailsImpl(customer1);
        reviewId = UUID.randomUUID();
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
            UUID.randomUUID(), customer1.getUserId(), requestDto.getStoreId(),
            requestDto.getOrderId(), requestDto.getProductId(),
            requestDto.getRating(), requestDto.getContents()
        );
        // given
        given(reviewService.createReview(customer1, requestDto))
            .willReturn(responseDto);
        // when
        ResponseEntity<ApiResponse<ReviewResponseDto>> rlt =
            reviewController.createReview(userDetails, requestDto);
        // then
        Assertions.assertEquals(HttpStatus.CREATED, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("리뷰 수정 성공")
    public void updateReview_success() {

        ReviewUpdateRequestDto requestDto = ReviewUpdateRequestDto
            .of(3, "음식이 식었어요!");
        ReviewResponseDto responseDto = new ReviewResponseDto(
            reviewId, customer1.getUserId(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(),
            requestDto.getRating(), requestDto.getContents()
        );
        // given
        given(reviewService.updateReview(customer1, reviewId, requestDto))
            .willReturn(responseDto);
        // when
        ResponseEntity<ApiResponse<ReviewResponseDto>> rlt =
            reviewController.updateReview(userDetails, reviewId, requestDto);
        // then
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("리뷰 삭제 성공")
    public void deleteReview_success() {
        // given
        doNothing().when(reviewService).deleteReview(customer1, reviewId);
        // when
        ResponseEntity<ApiResponse<Void>> rlt =
            reviewController.deleteReview(userDetails, reviewId);
        // then
        Assertions.assertEquals(HttpStatus.NO_CONTENT, rlt.getStatusCode());
        verify(reviewService).deleteReview(customer1, reviewId);
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    public void searchReview_success() throws Exception {
        ReviewSearchResponseDto responseDto = new ReviewSearchResponseDto(
            reviewId, 3, "배송이 늦었어요!", "몽룡이네 가게"
        );
        Page<ReviewSearchResponseDto> page = new PageImpl<>(List.of(responseDto));
        // given
        given(reviewService.searchReview(eq(customer1), any(), any()))
            .willReturn(page);
        // when
        ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> rlt =
            reviewController.searchReview(userDetails, "몽룡이네 가게", pageable);
        // then
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(),
            rlt.getBody().getData().getContent().get(0).getRating());
        Assertions.assertEquals(responseDto.getContents(),
            rlt.getBody().getData().getContent().get(0).getContents());
        verify(reviewService, times(1))
            .searchReview(eq(customer1), any(), any());
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    public void searchDetailsReview_success() {
        ReviewSearchResponseDto responseDto = new ReviewSearchResponseDto(
            reviewId, 3, "배송이 늦었어요!", "몽룡이네 가게"
        );
        // given
        given(reviewService.searchDetailReview(reviewId))
            .willReturn(responseDto);
        // when
        ResponseEntity<ApiResponse<ReviewSearchResponseDto>> rlt =
            reviewController.searchDetailReview(reviewId);
        // then
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getRating(), rlt.getBody().getData().getRating());
        Assertions.assertEquals(responseDto.getContents(), rlt.getBody().getData().getContents());
    }
}