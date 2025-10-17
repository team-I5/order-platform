package com.spartaclub.orderplatform.domain.review.application.service;

import static com.spartaclub.orderplatform.domain.review.exception.ReviewErrorCode.ALREADY_EXIST_IN_REVIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.UUID;
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

    // 필드 선언
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;
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
        // 사용자 객체 설정
        customer1 = new User();
        customer1.setUserId(1L);
        customer1.setUsername("guildong");
        customer1.setEmail("hong@test.com");
        customer1.setPassword("Hash123!");
        customer1.setNickname("gdong");
        customer1.setPhoneNumber("01012345678");
        customer1.setRole(UserRole.CUSTOMER);

        customer2 = new User();
        customer2.setUserId(2L);
        customer2.setUsername("customer2");
        customer2.setEmail("customer2@test.com");
        customer2.setPassword("Hashpassword2");
        customer2.setNickname("고객2");
        customer2.setPhoneNumber("01056781234");
        customer2.setRole(UserRole.CUSTOMER);

        owner1 = new User();
        owner1.setUserId(11L);
        owner1.setUsername("owner1");
        owner1.setEmail("owner1@test.com");
        owner1.setPassword("Hashpassword3");
        owner1.setNickname("오너1");
        owner1.setPhoneNumber("01067892345");
        owner1.setRole(UserRole.OWNER);
        owner1.setBusinessNumber("1234567890");

        owner2 = new User();
        owner2.setUserId(12L);
        owner2.setUsername("owner2");
        owner2.setEmail("owner2@test.com");
        owner2.setPassword("Hashpassword4");
        owner2.setNickname("오너2");
        owner2.setPhoneNumber("01086429743");
        owner2.setRole(UserRole.OWNER);
        owner2.setBusinessNumber("3456789012");

        manager1 = new User();
        manager1.setUserId(100L);
        manager1.setUsername("manager1");
        manager1.setEmail("manager1@test.com");
        manager1.setPassword("Hashpassword5");
        manager1.setNickname("매니저1");
        manager1.setPhoneNumber("01098765432");
        manager1.setRole(UserRole.MANAGER);
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
        System.out.println(assertThat(responseDto.getUserId()).isEqualTo(customer1.getUserId()));
        // 리뷰 중복 검사
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("리뷰 생성 실패")
    void createReview_fail_duplicateReview() {
        reviewService.createReview(customer1, requestDto);
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> reviewService.createReview(customer1, requestDto)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ALREADY_EXIST_IN_REVIEW);
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
