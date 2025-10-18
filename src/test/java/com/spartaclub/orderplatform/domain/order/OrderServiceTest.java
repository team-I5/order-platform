package com.spartaclub.orderplatform.domain.order;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.mock;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.application.dto.StoreInfoDto;
import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.application.query.StoreQuery;
import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ProductReaderRepository productReaderRepository;
    @Mock
    private StoreReaderRepository storeReaderRepository;

    // spy로 만들어 loadStoreSummaryInfo만 스텁해 흐름 단순화
    @Spy
    @InjectMocks
    private OrderService orderService;

    private final UUID STORE_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();

    private User mockUser(long id) {
        User u = mock(User.class);
        return u;
    }

    @BeforeEach
    void setUp() {
        // 필요 시 공통 stubbing
    }

    // ---------------------------------------------------------
    // placeOrder
    // ---------------------------------------------------------
    @Test
    @DisplayName("주문 생성 성공: products 로딩 → commands 매핑 → 저장 후 orderId 반환")
    void placeOrder_success() {
        // given
        User user = mockUser(10L);

        List<OrderItemRequest> items = List.of(
            new OrderItemRequest(UUID.randomUUID(), 1),
            new OrderItemRequest(UUID.randomUUID(), 2)
        );
        PlaceOrderRequestDto req = new PlaceOrderRequestDto(STORE_ID, "서울",
            items, "메모");

        // productReaderRepository → id 목록에 맞는 Product mocks
        List<Product> foundProducts = items.stream().map(i -> {
            Product p = mock(Product.class);
            given(p.getProductId()).willReturn(i.productId());
            return p;
        }).collect(toList());
        given(productReaderRepository.findByProductIdIn(anyList())).willReturn(foundProducts);

        // mapper → 각 item을 command로 매핑
        for (OrderItemRequest it : items) {
            given(orderMapper.toCommand(it))
                .willReturn(new PlaceOrderCommand(it.productId(), it.quantity()));
        }

        // loadStoreSummaryInfo(query, userId) → spy 메서드 스텁
        StoreInfoDto storeInfo = mock(StoreInfoDto.class);
        given(storeInfo.storeId()).willReturn(STORE_ID);
        doReturn(storeInfo).when(orderService)
            .loadStoreSummaryInfo(any(StoreQuery.class), anyLong());

        // 저장 시 orderId가 세팅된 Order 반환
        Order saved = mock(Order.class);
        given(saved.getOrderId()).willReturn(ORDER_ID);
        given(orderRepository.save(any(Order.class))).willReturn(saved);

        // when
        PlaceOrderResponseDto resp = orderService.placeOrder(req, user);

        // then
        assertThat(resp).isNotNull();
        assertThat(resp.orderId()).isEqualTo(ORDER_ID);

        // verify - 필수 상호작용
        verify(productReaderRepository).findByProductIdIn(argThat(list -> list.containsAll(
            items.stream().map(OrderItemRequest::productId).toList())));
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper, times(items.size())).toCommand(any(OrderItemRequest.class));
    }

    // ---------------------------------------------------------
    // cancelOrder
    // ---------------------------------------------------------
    @Test
    @DisplayName("주문 취소: 취소 가능 검증 후 CANCELED로 상태 변경")
    void cancelOrder_success() {
        // given
        User domainUser = mockUser(99L);
        UserDetailsImpl principal = mock(UserDetailsImpl.class);

        Order order = mock(Order.class);
        doNothing().when(order).checkCancelable();

        doReturn(order).when(orderService).findById(ORDER_ID);

        // when
        OrderStatusResponseDto resp = orderService.cancelOrder(principal, ORDER_ID);

        // then
        assertThat(resp.orderId()).isEqualTo(ORDER_ID);
        assertThat(resp.status()).isEqualTo(OrderStatus.CANCELED);
        verify(order).checkCancelable();
        verify(order).changeStatus(OrderStatus.CANCELED);
    }

    // ---------------------------------------------------------
    // acceptOrder
    // ---------------------------------------------------------
    @Test
    @DisplayName("주문 승인: 검증 후 ACCEPTED로 상태 변경")
    void acceptOrder_success() {
        // given
        Order order = mock(Order.class);
        doNothing().when(order).checkAcceptable();

        doReturn(order).when(orderService).findById(ORDER_ID);

        // when
        OrderStatusResponseDto resp = orderService.acceptOrder(mock(UserDetailsImpl.class),
            ORDER_ID);

        // then
        assertThat(resp.status()).isEqualTo(OrderStatus.ACCEPTED);
        verify(order).checkAcceptable();
        verify(order).changeStatus(OrderStatus.ACCEPTED);
    }

    // ---------------------------------------------------------
    // rejectOrder
    // ---------------------------------------------------------
    @Test
    @DisplayName("주문 거부: 검증 후 REJECTED로 상태 변경")
    void rejectOrder_success() {
        // given
        Order order = mock(Order.class);
        doNothing().when(order).checkRejectable();

        doReturn(order).when(orderService).findById(ORDER_ID);

        // when
        OrderStatusResponseDto resp = orderService.rejectOrder(mock(UserDetailsImpl.class),
            ORDER_ID);

        // then
        assertThat(resp.status()).isEqualTo(OrderStatus.REJECTED);
        verify(order).checkRejectable();
        verify(order).changeStatus(OrderStatus.REJECTED);
    }

    // ---------------------------------------------------------
    // completeDelivery
    // ---------------------------------------------------------
    @Test
    @DisplayName("배달 완료: 검증 후 DELIVERED로 상태 변경")
    void completeDelivery_success() {
        // given
        Order order = mock(Order.class);
        doNothing().when(order).checkDeliverable();

        doReturn(order).when(orderService).findById(ORDER_ID);

        // when
        OrderStatusResponseDto resp = orderService.completeDelivery(mock(UserDetailsImpl.class),
            ORDER_ID);

        // then
        assertThat(resp.status()).isEqualTo(OrderStatus.DELIVERED);
        verify(order).checkDeliverable();
        verify(order).changeStatus(OrderStatus.DELIVERED);
    }
}

