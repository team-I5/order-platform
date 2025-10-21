package com.spartaclub.orderplatform.domain.review.application.service;

import static com.spartaclub.orderplatform.domain.review.exception.ReviewErrorCode.ALREADY_EXIST_IN_REVIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    // 외래 키 관계 repository
    @Mock
    private StoreService storeService;

    @Mock
    private ProductService productService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    // 외래키 관계 Entity
    private User customer1;
    private User owner1;
    private User manager1;
    private Store store1;
    private Product product1;
    private Product product2;
    private Order order1;
    private Order order2;
    private Review review1;
    private Review review2;
    private UserDetailsImpl userDetails;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        // 사용자 생성
        customer1 = User.createUser(
            "customer1",
            "customer1@test.com",
            "Hashpassword1",
            "고객1",
            "01012345678",
            UserRole.CUSTOMER
        );
        ReflectionTestUtils.setField(customer1, "userId", 1L);
        owner1 = User.createBusinessUser(
            "owner1",
            "owner1@test.com",
            "Hashpassword2",
            "주인1",
            "01087659423",
            UserRole.OWNER,
            "1234567890"
        );
        ReflectionTestUtils.setField(owner1, "userId", 3L);
        manager1 = User.createManager(
            "owner1",
            "owner1@test.com",
            "Hashpassword3",
            "매니저1",
            "01056782394"
        );
        ReflectionTestUtils.setField(manager1, "role", UserRole.MANAGER);
        // 가게 생성
        // Dto
        StoreRequestDto storeRequestDto1 = new StoreRequestDto();
        storeRequestDto1.setStoreName("store1");
        storeRequestDto1.setStoreAddress("address1");
        storeRequestDto1.setStoreNumber("01234543210");
        storeRequestDto1.setStoreDescription("localStore");
        store1 = Store.create(owner1, storeRequestDto1);
        ReflectionTestUtils.setField(store1, "storeId", UUID.randomUUID());

        // 상품 생성
        product1 = Product.create("product1", 10000L,
            "product1description", store1);
        ReflectionTestUtils.setField(product1, "productId", UUID.randomUUID());
        product2 = Product.create("product2", 15000L,
            "product2description", store1);
        ReflectionTestUtils.setField(product2, "productId", UUID.randomUUID());
        // 주문 생성
        order1 = Order.place(customer1, store1.getStoreId(),
            List.of(new PlaceOrderCommand(product1.getProductId(), 2)),
            Map.of(product1.getProductId(), product1), "address1", "memo1");
        ReflectionTestUtils.setField(order1, "orderId", UUID.randomUUID());
        order2 = Order.place(customer1, store1.getStoreId(),
            List.of(new PlaceOrderCommand(product2.getProductId(), 2)),
            Map.of(product2.getProductId(), product2), "address1", "memo1");
        ReflectionTestUtils.setField(order2, "orderId", UUID.randomUUID());
        // 리뷰 Entity
        review1 = Review.create(customer1, store1, product1, order1, 4, "배달이 빨랐어요!");
        ReflectionTestUtils.setField(review1, "reviewId", UUID.randomUUID());
        review2 = Review.create(customer1, store1, product2, order2, 5, "맛이 좋네요!");
        ReflectionTestUtils.setField(review2, "reviewId", UUID.randomUUID());
        userDetails = new UserDetailsImpl(customer1);
    }

    @Test
    @DisplayName("service - 리뷰 생성 실패(리뷰 중복)")
    void createReview_fail() {
        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
            store1.getStoreId(),
            order1.getOrderId(),
            product1.getProductId(),
            4,
            "배달이 빨랐어요!"
        );
        // 리뷰 생성
        // given
        given(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(order1.getOrderId()))
            .willReturn(true);
        // when
        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> reviewService.createReview(userDetails.getUser(), requestDto));
        // then
        System.out.println("exception.getErrorCode() = " + exception.getErrorCode());
        Assertions.assertThat(exception.getErrorCode())
            .isEqualTo(ALREADY_EXIST_IN_REVIEW);
    }

    @Test
    @DisplayName("service - 리뷰 생성 성공")
    void createReview_success() {
        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
            store1.getStoreId(),
            order2.getOrderId(),
            product2.getProductId(),
            5,
            "맛이 좋네요!"
        );
        // 리뷰 생성
        // given
        given(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(order2.getOrderId()))
            .willReturn(false);
        given(storeService.getStore(store1.getStoreId())).willReturn(store1);
        given(productService.findProductOrThrow(product2.getProductId())).willReturn(product2);
        given(orderService.findById(order2.getOrderId())).willReturn(order2);
        // save 호출할 때 review 반환
        given(reviewRepository.save(any(Review.class))).willReturn(review2);
        given(reviewMapper.toReviewResponseDto(any(Review.class)))
            .willReturn(new ReviewResponseDto(
                review2.getReviewId(),
                review2.getUser().getUserId(),
                review2.getStore().getStoreId(),
                review2.getOrder().getOrderId(),
                review2.getProduct().getProductId(),
                review2.getRating(),
                review2.getContents()
            ));
        // when
        ReviewResponseDto responseDto = reviewService.createReview(userDetails.getUser(),
            requestDto);
        // then
        Assertions.assertThat(responseDto.getReviewId()).isEqualTo(review2.getReviewId());
        Assertions.assertThat(responseDto.getRating()).isEqualTo(review2.getRating());
        Assertions.assertThat(responseDto.getContents()).isEqualTo(review2.getContents());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(reviewMapper, times(1)).toReviewResponseDto(any(Review.class));
    }

    @Test
    @DisplayName("service - 리뷰 수정")
    void updateReview_success() {
        ReviewUpdateRequestDto updateDto = ReviewUpdateRequestDto.of(3, "음식이 식었어요!");
        given(reviewRepository.findById(review1.getReviewId())).willReturn(Optional.of(review1));
        given(reviewMapper.toReviewResponseDto(review1)).willReturn(
            new ReviewResponseDto(
                review1.getReviewId(),
                review1.getUser().getUserId(),
                review1.getStore().getStoreId(),
                review1.getOrder().getOrderId(),
                review1.getProduct().getProductId(),
                updateDto.getRating(),
                updateDto.getContents()
            )
        );
        ReviewResponseDto responseDto = reviewService.updateReview(userDetails.getUser(),
            review1.getReviewId(), updateDto);
        Assertions.assertThat(responseDto.getRating()).isEqualTo(review1.getRating());
        Assertions.assertThat(responseDto.getContents()).isEqualTo(review1.getContents());
    }

    @Test
    @DisplayName("Service - 리뷰 삭제")
    void deleteReview_success() {
        given(reviewRepository.findById(review1.getReviewId())).willReturn(Optional.of(review1));
        reviewService.deleteReview(userDetails.getUser(), review1.getReviewId());
        Assertions.assertThat(review1.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Service - Customer 권한 리뷰 목록 조회")
    void searchReviewForCustomer_success() {
        Page<Review> reviewPage = new PageImpl<>(List.of(review1));
        given(reviewRepository.findByUser_UserIdAndDeletedAtIsNull(customer1.getUserId(), pageable))
            .willReturn(reviewPage);
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                review1.getStore().getStoreName()
            ));
        Page<ReviewSearchResponseDto> rlt = reviewService.searchReview(customer1, null, pageable);
        Assertions.assertThat(rlt).hasSize(1);
        Assertions.assertThat(rlt.getContent().get(0).getRating()).isEqualTo(4);
        Assertions.assertThat(rlt.getContent().get(0).getContents()).isEqualTo("배달이 빨랐어요!");
        verify(reviewRepository, times(1))
            .findByUser_UserIdAndDeletedAtIsNull(customer1.getUserId(), pageable);
    }

    @Test
    @DisplayName("Service - Owner 권한 리뷰 목록 조회")
    void searchReviewForOwner_success() {
        Page<Review> reviewPage = new PageImpl<>(List.of(review1));
        given(reviewRepository.findByStore_StoreNameAndDeletedAtIsNull(store1.getStoreName(),
            pageable))
            .willReturn(reviewPage);
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                store1.getStoreName()
            ));
        Page<ReviewSearchResponseDto> rlt = reviewService.searchReview(owner1,
            store1.getStoreName(), pageable);
        Assertions.assertThat(rlt).hasSize(1);
        Assertions.assertThat(rlt.getContent().get(0).getRating()).isEqualTo(4);
        Assertions.assertThat(rlt.getContent().get(0).getContents()).isEqualTo("배달이 빨랐어요!");
        verify(reviewRepository, times(1))
            .findByStore_StoreNameAndDeletedAtIsNull(store1.getStoreName(), pageable);
    }

    @Test
    @DisplayName("Service - Manager 권한 리뷰 목록 조회")
    void searchReviewForManager_success() {
        Page<Review> reviewPage = new PageImpl<>(List.of(review1));
        given(reviewRepository.findByDeletedAtIsNull(pageable))
            .willReturn(reviewPage);
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                store1.getStoreName()
            ));
        Page<ReviewSearchResponseDto> rlt = reviewService.searchReview(manager1,
            null, pageable);
        Assertions.assertThat(rlt).hasSize(1);
        Assertions.assertThat(rlt.getContent().get(0).getRating()).isEqualTo(4);
        Assertions.assertThat(rlt.getContent().get(0).getContents()).isEqualTo("배달이 빨랐어요!");
        verify(reviewRepository, times(1))
            .findByDeletedAtIsNull(pageable);

    }

    @Test
    @DisplayName("Service - 리뷰 상세 조회")
    void searchReviewDetail_success() {
        given(reviewRepository.findById(review1.getReviewId()))
            .willReturn(Optional.of(review1));
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                review1.getStore().getStoreName()
            ));
        ReviewSearchResponseDto rlt = reviewService.searchDetailReview(review1.getReviewId());
        Assertions.assertThat(rlt.getReviewId()).isEqualTo(review1.getReviewId());
        Assertions.assertThat(rlt.getRating()).isEqualTo(review1.getRating());
        Assertions.assertThat(rlt.getContents()).isEqualTo(review1.getContents());
        verify(reviewRepository, times(1))
            .findById(review1.getReviewId());
    }

    @Test
    @DisplayName("Service - 리뷰 조건별 조회(rating 기준)")
    void searchConditionReviewOneByRating_success() {
        Page<Review> reviewPage = new PageImpl<>(List.of(review1));
        given(reviewRepository.findByRatingAndDeletedAtIsNull(review1.getRating(), pageable))
            .willReturn(reviewPage);
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                review1.getStore().getStoreName()
            ));
        Page<ReviewSearchResponseDto> rlt = reviewService
            .searchConditionReviewOne(customer1, review1.getRating(), pageable);
        Assertions.assertThat(rlt).hasSize(1);
        Assertions.assertThat(rlt.getContent().get(0).getRating()).isEqualTo(4);
        Assertions.assertThat(rlt.getContent().get(0).getContents())
            .isEqualTo(review1.getContents());
        verify(reviewRepository, times(1))
            .findByRatingAndDeletedAtIsNull(review1.getRating(), pageable);
    }

    @Test
    @DisplayName("Service - 리뷰 조건별 조회(keyword 기준)")
    void searchConditionReviewTwoByKeyword_success() {
        Page<Review> reviewPage = new PageImpl<>(List.of(review1));
        given(reviewRepository.findByContentsContainingAndDeletedAtIsNull(review1.getContents(),
            pageable))
            .willReturn(reviewPage);
        given(reviewMapper.toReviewSearchResponseDto(review1))
            .willReturn(new ReviewSearchResponseDto(
                review1.getReviewId(),
                review1.getRating(),
                review1.getContents(),
                review1.getStore().getStoreName()
            ));
        Page<ReviewSearchResponseDto> rlt = reviewService
            .searchConditionReviewTwo(customer1, review1.getContents(), pageable);
        Assertions.assertThat(rlt).hasSize(1);
        Assertions.assertThat(rlt.getContent().get(0).getRating()).isEqualTo(4);
        Assertions.assertThat(rlt.getContent().get(0).getContents())
            .isEqualTo(review1.getContents());
        verify(reviewRepository, times(1))
            .findByContentsContainingAndDeletedAtIsNull(review1.getContents(), pageable);
    }
}
