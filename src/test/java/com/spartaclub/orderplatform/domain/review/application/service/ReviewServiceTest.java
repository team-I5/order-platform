//package com.spartaclub.orderplatform.domain.review.application.service;
//
//import static org.mockito.Mockito.when;
//
//import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
//import com.spartaclub.orderplatform.domain.order.domain.model.Order;
//import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
//import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
//import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
//import com.spartaclub.orderplatform.domain.review.domain.model.Review;
//import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
//import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
//import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
//import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
//import com.spartaclub.orderplatform.domain.store.domain.model.Store;
//import com.spartaclub.orderplatform.domain.user.domain.entity.User;
//import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
//import java.util.UUID;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class ReviewServiceTest {
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private ReviewMapper reviewMapper;
//
//    @Mock
//    private OrderService orderService;
//
//    @Mock
//    private StoreService storeService;
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    private User customer1;
//
//    @BeforeEach
//    void setUp() {
//        customer1 = User.createUser(
//            "customer1",
//            "customer1@test.com",
//            "Hashpassword1",
//            "고객1",
//            "01012345678",
//            UserRole.CUSTOMER
//        );
//
//    }
//
//    @Test
//    @DisplayName("service - 리뷰 생성")
//    void createReview() {
//
//        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
//            UUID.fromString("506772f3-6271-418e-a2c7-4fad65e22938"),
//            UUID.fromString("c0ac2c6e-e674-49e2-8a92-22f9e9518f4c"),
//            UUID.fromString("d429674f-6b54-4b4f-a08d-eb90078835b0"),
//            4,
//            "배달이 빨랐어요!"
//        );
//        // 주문 조회
//        Order order = orderService.findById(requestDto.getOrderId());
//        // 가게 조회
//        Store store = storeService.getStore(requestDto.getStoreId());
//        // 상품 조회
//        Product product = productService.findProductOrThrow(requestDto.getProductId());
//        // 2. 리뷰 객체 생성 By 정적 팩토리 메서드
//        Review review = Review.create(customer1, store, product, order, requestDto.getRating(),
//            requestDto.getContents());
//
//        ReviewResponseDto responseDto = new ReviewResponseDto(
//            review.getReviewId(), customer1.getUserId(),
//            store.getStoreId(), order.getOrderId(),
//            product.getProductId(), review.getRating(), review.getContents()
//        );
//
//        when(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(
//            requestDto.getOrderId())).thenReturn(false);
//        when(storeService.getStore(requestDto.getStoreId())).thenReturn(store);
//        when(productService.findProductOrThrow(requestDto.getProductId())).thenReturn(product);
//        when(orderService.findById(requestDto.getOrderId())).thenReturn(order);
//        when(reviewRepository.save(review)).thenReturn(review);
//        when(reviewMapper.toReviewResponseDto(review)).thenReturn(responseDto);
//        ReviewResponseDto rlt = reviewService.createReview(customer1, requestDto);
//        Assertions.assertEquals(responseDto.getRating(), rlt.getRating());
//        Assertions.assertEquals(responseDto.getContents(), rlt.getContents());
//    }
//}
