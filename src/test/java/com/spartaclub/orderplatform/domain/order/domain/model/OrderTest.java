package com.spartaclub.orderplatform.domain.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

class OrderTest {

    private User mockUser(long id) {
        User u = mock(User.class);
        when(u.getUserId()).thenReturn(id);
        return u;
    }

    private Product mockProduct(UUID id) {
        Product p = mock(Product.class);
        when(p.getProductId()).thenReturn(id);
        return p;
    }

    // -------------------------------
    // place()
    // -------------------------------
    @Test
    @DisplayName("place: 주문 생성 시 PENDING 상태, 합계/수량 계산, 연관 링크가 설정된다")
    void place_success_calculatesTotals_and_links() {
        // given
        User user = mockUser(1L);
        UUID storeId = UUID.randomUUID();

        UUID pid1 = UUID.randomUUID();
        UUID pid2 = UUID.randomUUID();

        List<PlaceOrderCommand> commands = List.of(
            new PlaceOrderCommand(pid1, 1),
            new PlaceOrderCommand(pid2, 2)
        );

        Map<UUID, Product> products = Map.of(
            pid1, mockProduct(pid1),
            pid2, mockProduct(pid2)
        );

        // OrderProduct.of(...) 이 반환할 모의 객체 준비 (단가/수량 반환값만 사용됨)
        var op1 = mock(OrderProduct.class);
        when(op1.getUnitPrice()).thenReturn(1_000L);
        when(op1.getQuantity()).thenReturn(1);

        var op2 = mock(OrderProduct.class);
        when(op2.getUnitPrice()).thenReturn(2_000L);
        when(op2.getQuantity()).thenReturn(2);

        try (MockedStatic<OrderProduct> mocked = mockStatic(OrderProduct.class)) {
            mocked.when(() -> OrderProduct.of(any(Product.class), eq(1), any(Order.class)))
                .thenReturn(op1);
            mocked.when(() -> OrderProduct.of(any(Product.class), eq(2), any(Order.class)))
                .thenReturn(op2);

            // when
            Order order = Order.place(user, storeId, commands, products, "서울", "메모");

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
            assertThat(order.getOrderProducts()).hasSize(2);
            assertThat(order.getTotalPrice()).isEqualTo(1_000L * 1 + 2_000L * 2); // 5,000
            assertThat(order.getProductCount()).isEqualTo(3);
            assertThat(order.getAddress()).isEqualTo("서울");
            assertThat(order.getMemo()).isEqualTo("메모");
            assertThat(order.getUser()).isSameAs(user);
            assertThat(order.getStoreId()).isEqualTo(storeId);
        }
    }

    // -------------------------------
    // validatePaymentAvailable()
    // -------------------------------
    @Test
    @DisplayName("validatePaymentAvailable: PENDING 상태 + 금액 일치 → 통과")
    void validatePaymentAvailable_ok() {
        User user = mockUser(1L);
        UUID storeId = UUID.randomUUID();

        // 주문 하나 생성(합계=3000)
        List<PlaceOrderCommand> commands = List.of(new PlaceOrderCommand(UUID.randomUUID(), 3));
        Map<UUID, Product> products = Map.of(commands.get(0).productId(),
            mockProduct(commands.get(0).productId()));

        var op = mock(OrderProduct.class);
        when(op.getUnitPrice()).thenReturn(1_000L);
        when(op.getQuantity()).thenReturn(3);

        try (MockedStatic<OrderProduct> mocked = mockStatic(OrderProduct.class)) {
            mocked.when(() -> OrderProduct.of(any(), eq(3), any(Order.class))).thenReturn(op);

            Order order = Order.place(user, storeId, commands, products, "addr", null);
            assertThatCode(() -> order.validatePaymentAvailable(3_000L))
                .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("validatePaymentAvailable: PENDING 아님 → 예외")
    void validatePaymentAvailable_invalidStatus() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAID);
        ReflectionTestUtils.setField(order, "totalPrice", 10_000L);

        assertThatThrownBy(() -> order.validatePaymentAvailable(10_000L))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("validatePaymentAvailable: 금액 불일치 → 예외")
    void validatePaymentAvailable_amountMismatch() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        ReflectionTestUtils.setField(order, "totalPrice", 10_000L);

        assertThatThrownBy(() -> order.validatePaymentAvailable(9_999L))
            .isInstanceOf(BusinessException.class);
    }

    // -------------------------------
    // checkCancelable()
    // -------------------------------
    @Test
    @DisplayName("checkCancelable: PENDING + 5분 이내 → 통과")
    void checkCancelable_ok_withinWindow() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now().minusMinutes(4));

        assertThatCode(order::checkCancelable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("checkCancelable: 상태가 PENDING도 아니고 PAID도 아니면 → 예외")
    void checkCancelable_invalidStatus() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.ACCEPTED);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now());

        assertThatThrownBy(order::checkCancelable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("checkCancelable: 생성 시간 누락 → 예외")
    void checkCancelable_missingCreatedAt() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        // createdAt 미설정
        assertThatThrownBy(order::checkCancelable).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("checkCancelable: 5분 초과 → 예외")
    void checkCancelable_windowExpired() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now().minusMinutes(6));

        assertThatThrownBy(order::checkCancelable).isInstanceOf(BusinessException.class);
    }

    // -------------------------------
    // checkAcceptable()
    // -------------------------------
    @Test
    @DisplayName("checkAcceptable: PAID 상태면 통과")
    void checkAcceptable_ok_whenPaid() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAID);
        assertThatCode(order::checkAcceptable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("checkAcceptable: PAID가 아니면 예외")
    void checkAcceptable_invalid() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        assertThatThrownBy(order::checkAcceptable).isInstanceOf(BusinessException.class);
    }

    // -------------------------------
    // checkRejectable()
    // -------------------------------
    @Test
    @DisplayName("checkRejectable: PENDING이면 통과")
    void checkRejectable_ok_whenPending() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        assertThatCode(order::checkRejectable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("checkRejectable: PAID면 통과")
    void checkRejectable_ok_whenPaid() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAID);
        assertThatCode(order::checkRejectable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("checkRejectable: PENDING도 아니고 PAID도 아니면 예외")
    void checkRejectable_invalid() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.ACCEPTED);
        assertThatThrownBy(order::checkRejectable).isInstanceOf(BusinessException.class);
    }

    // -------------------------------
    // checkDeliverable()
    // -------------------------------
    @Test
    @DisplayName("checkDeliverable: ACCEPTED면 통과")
    void checkDeliverable_ok_whenAccepted() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.ACCEPTED);
        assertThatCode(order::checkDeliverable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("checkDeliverable: ACCEPTED가 아니면 예외")
    void checkDeliverable_invalid() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        assertThatThrownBy(order::checkDeliverable).isInstanceOf(BusinessException.class);
    }

    // -------------------------------
    // changeStatus()
    // -------------------------------
    @Test
    @DisplayName("changeStatus: 상태가 변경된다")
    void changeStatus_updatesField() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_PENDING);
        order.changeStatus(OrderStatus.CANCELED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }
}

