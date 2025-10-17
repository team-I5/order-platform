package com.spartaclub.orderplatform.domain.review.application.service;

import static com.spartaclub.orderplatform.domain.review.exception.ReviewErrorCode.ALREADY_EXIST_IN_REVIEW;

import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.user.application.service.UserService;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ReviewServiceTest {

    // Mock 객체 정의
    // 필드 선언

    // 저장소 Mock
    // DB데이터 접근 계층
    @Autowired
    private ReviewRepository reviewRepository;

    // 맵퍼 Mock
    @Autowired
    private ReviewMapper reviewMapper;

    // 테스트 target 서비스(Mock 객체들이 서비스에 주입됨)
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;


    // 필요 클래스 객체 변수 선언
    private User customer1;
    private User customer2;
    private User owner1;
    private User owner2;
    private User manager1;
    // 필요 dto 선언
    private ReviewCreateRequestDto requestDto;
    private ReviewUpdateRequestDto requestUpdateDto;


    // 테스트 메서드 실행 전 사전 작업
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

        customer2 = User.createUser(
            "customer2",
            "customer2@test.com",
            "Hashpassword2",
            "고객2",
            "01056781234",
            UserRole.CUSTOMER
        );

        owner1 = User.createBusinessUser(
            "owner1",
            "owner1@test.com",
            "Hashpassword3",
            "오너1",
            "01067892345",
            UserRole.OWNER,
            "1234567890"
        );

        owner2 = User.createBusinessUser(
            "owner2",
            "owner2@test.com",
            "Hashpassword4",
            "오너2",
            "01086429743",
            UserRole.OWNER,
            "3456789012"
        );

        // 필요 dto 객체 설정
        // 리뷰 생성
        requestDto = new ReviewCreateRequestDto();
        requestDto.setStoreId(UUID.fromString("506772f3-6271-418e-a2c7-4fad65e22938"));
        requestDto.setOrderId(UUID.fromString("c0ac2c6e-e674-49e2-8a92-22f9e9518f4c"));
        requestDto.setProductId(UUID.fromString("3ac3e6a5-5d20-42cb-b698-ae932c195e23"));
        requestDto.setRating(4);
        requestDto.setContents("맛이 좋았습니다.");
        // 리뷰 수정
        requestUpdateDto = new ReviewUpdateRequestDto();
        requestUpdateDto.setRating(3);
        requestUpdateDto.setContents("음식이 식었어요.");


    }

    @Test
    @WithMockUser(username = "customer1", roles = {"CUSTOMER"})
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        ReviewResponseDto responseDto = reviewService.createReview(customer1, requestDto);
        System.out.println("responseDto.getRating() = " + responseDto.getRating());
        System.out.println("responseDto.getContents() = " + responseDto.getContents());
        System.out.println(
            Assertions.assertThat(responseDto.getUserId()).isEqualTo(customer1.getUserId()));
        // 리뷰 중복 검사
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("리뷰 생성 실패")
    void createReview_fail_duplicateReview() {
        reviewService.createReview(customer1, requestDto);
        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> reviewService.createReview(customer1, requestDto)
        );
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ALREADY_EXIST_IN_REVIEW);
    }

    @Test
    @WithMockUser(username = "customer1", roles = {"CUSTOMER"})
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() {

    }


    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("리뷰 수정 실패")
    void updateReview_fail() {

    }


}
