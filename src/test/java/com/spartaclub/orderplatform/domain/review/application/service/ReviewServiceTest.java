//package com.spartaclub.orderplatform.domain.review.application.service;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.when;
//
//import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
//import com.spartaclub.orderplatform.domain.order.domain.model.Order;
//import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
//import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto.OrderItemRequest;
//import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
//import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
//import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductCreateRequestDto;
//import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
//import com.spartaclub.orderplatform.domain.review.domain.model.Review;
//import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
//import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
//import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
//import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
//import com.spartaclub.orderplatform.domain.store.domain.model.Store;
//import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
//import com.spartaclub.orderplatform.domain.user.domain.entity.User;
//import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
//import java.util.List;
//import java.util.UUID;
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
//
//    private User customer1;
//    private User owner1;
//    private Store store1;
//    private Product product1;
//    private Order order1;
//    private StoreRequestDto storeRequestDto;
//    private ProductCreateRequestDto productCreateRequestDto;
//    private PlaceOrderRequestDto orderRequestDto;
//    private ReviewCreateRequestDto reviewCreateRequestDto;
//    private UUID orderId=UUID.randomUUID();
//    private UUID storeId=UUID.randomUUID();
//    private UUID productId=UUID.randomUUID();
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
//        owner1 = User.createBusinessUser(
//            "owner1",
//            "owner1@test.com",
//            "Hashpassword2",
//            "주인1",
//            "01087659423",
//            UserRole.OWNER,
//            "1234567890"
//        );
//        storeRequestDto = new StoreRequestDto();
//        storeRequestDto.setStoreName("store1");
//        storeRequestDto.setStoreAddress("address1");
//        storeRequestDto.setStoreNumber("01234543210");
//        storeRequestDto.setStoreDescription("localStore");
//        store1 = Store.create(owner1, storeRequestDto);
//        storeId = store1.getStoreId();
//        product1 = Product.create("product1", 10000L, "product1description", store1);
//        List<OrderItemRequest> items = List.of(
//            new OrderItemRequest(UUID.randomUUID(), 1),
//            new OrderItemRequest(UUID.randomUUID(), 2)
//        );
//        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto(storeId, "서울",
//            items, "메모");
//
//        order1 = Order.place(customer1,storeId,items,pro)
//    }
//
//    @Test
//    @DisplayName("service - 리뷰 생성")
//    void createReview() {
//
//        ReviewCreateRequestDto requestDto = ReviewCreateRequestDto.of(
//
//            4,
//            "배달이 빨랐어요!"
//        );
//
//        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(UUID.randomUUID(),
//            customer1.getUserId(), store1.getStoreId(), order1.getOrderId(),
//            product1.getProductId(), 4, "배달이 빨랐어요!");
/// /        // 주문 조회
//        order1 = orderService.findById(requestDto.getOrderId());
////        // 가게 조회
////        Store store = storeService.getStore(requestDto.getStoreId());
////        // 상품 조회
////        Product product = productService.findProductOrThrow(requestDto.getProductId());
//
////        ReviewResponseDto rlt = reviewService.createReview()
//
//        // 2. 리뷰 객체 생성 By 정적 팩토리 메서드
//        Review review = Review.create(customer1, store1, product1, order1, requestDto.getRating(),
//            requestDto.getContents());
//
//        ReviewResponseDto responseDto = new ReviewResponseDto(
//            review.getReviewId(), customer1.getUserId(),
//            store1.getStoreId(), order1.getOrderId(),
//            product1.getProductId(), review.getRating(), review.getContents()
//        );
//
//        when(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(
//            requestDto.getOrderId())).thenReturn(false);
//        when(storeService.getStore(requestDto.getStoreId())).thenReturn(store1);
//        when(productService.findProductOrThrow(requestDto.getProductId())).thenReturn(product1);
//        when(orderService.findById(requestDto.getOrderId())).thenReturn(order1);
//        when(reviewRepository.save(review)).thenReturn(review);
//        when(reviewMapper.toReviewResponseDto(review)).thenReturn(responseDto);
//
////        given(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(orderId))
//
//        given(reviewRepository.save(any(Review.class))).willReturn(review);
////        ReviewResponseDto rlt = reviewService.createReview(customer1, requestDto);
////        Assertions.assertEquals(responseDto.getRating(), rlt.getRating());
////        Assertions.assertEquals(responseDto.getContents(), rlt.getContents());
//    }
//}
